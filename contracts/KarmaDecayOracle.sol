// SPDX-License-Identifier: MIT
pragma solidity ^0.8.23;

import "@openzeppelin/contracts/access/Ownable.sol";
import "./SoulContract.sol";

contract KarmaDecayOracle is Ownable {
    SoulContract public soulContract;
    uint256 public constant DECAY_RATE_BPS = 42; // 0.42% per day
    uint256 public lastDecayTimestamp;

    event KarmaDecayed(uint256 indexed tokenId, int256 oldKarma, int256 newKarma);

    constructor(address _soulContract) Ownable(msg.sender) {
        soulContract = SoulContract(_soulContract);
        lastDecayTimestamp = block.timestamp;
    }

    function decayAllEligible() external onlyOwner {
        uint256 timeElapsed = block.timestamp - lastDecayTimestamp;
        uint256 daysElapsed = timeElapsed / 1 days;

        if (daysElapsed == 0) return;

        // In real version: loop over active tokens or use merkle proof batch
        // Here: demo single token
        uint256 tokenId = 1;
        int256 current = soulContract.karmaBalance(tokenId);
        int256 decay = (current * int256(DECAY_RATE_BPS) * int256(daysElapsed)) / 10000;
        int256 newKarma = current - decay;

        soulContract.updateKarma(tokenId, newKarma);
        emit KarmaDecayed(tokenId, current, newKarma);

        lastDecayTimestamp = block.timestamp;
    }
}
