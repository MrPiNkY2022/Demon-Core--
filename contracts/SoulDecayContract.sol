// SPDX-License-Identifier: MIT
pragma solidity ^0.8.23;

import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/utils/math/SafeMath.sol";

interface ISoulContract {
    function karmaBalance(uint256 tokenId) external view returns (int256);
    function updateKarma(uint256 tokenId, int256 newKarma) external;
}

contract SoulDecayContract is Ownable {
    using SafeMath for uint256;

    ISoulContract public soulContract;
    uint256 public constant DECAY_RATE_BPS = 42; // 0.42% per day
    uint256 public constant MAX_DECAY_DAYS = 365;
    uint256 public lastDecayTimestamp;
    mapping(uint256 => uint256) public lastDecayPerToken;

    event KarmaDecayed(uint256 indexed tokenId, int256 oldKarma, int256 decayAmount);

    constructor(address _soulContract) Ownable(msg.sender) {
        soulContract = ISoulContract(_soulContract);
        lastDecayTimestamp = block.timestamp;
    }

    function triggerDecay(uint256[] calldata tokenIds) external onlyOwner {
        uint256 currentTime = block.timestamp;
        uint256 daysElapsed = (currentTime - lastDecayTimestamp) / 1 days;

        if (daysElapsed == 0) return;

        for (uint256 i = 0; i < tokenIds.length; i++) {
            uint256 tokenId = tokenIds[i];
            uint256 last = lastDecayPerToken[tokenId];
            uint256 effectiveDays = (currentTime - last) / 1 days;
            if (effectiveDays == 0) continue;

            int256 current = soulContract.karmaBalance(tokenId);
            uint256 decayBps = DECAY_RATE_BPS.mul(effectiveDays.min(MAX_DECAY_DAYS));
            int256 decay = (current * int256(decayBps)) / 10000;
            int256 newKarma = current - decay;

            soulContract.updateKarma(tokenId, newKarma);
            lastDecayPerToken[tokenId] = currentTime;

            emit KarmaDecayed(tokenId, current, decay);
        }

        lastDecayTimestamp = currentTime;
    }

    function getPendingDecay(uint256 tokenId) external view returns (int256) {
        uint256 daysElapsed = (block.timestamp - lastDecayPerToken[tokenId]) / 1 days;
        int256 current = soulContract.karmaBalance(tokenId);
        uint256 decayBps = DECAY_RATE_BPS.mul(daysElapsed.min(MAX_DECAY_DAYS));
        return (current * int256(decayBps)) / 10000;
    }
}
