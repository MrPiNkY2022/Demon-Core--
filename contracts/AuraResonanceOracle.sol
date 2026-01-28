// SPDX-License-Identifier: MIT
pragma solidity ^0.8.23;

import "@openzeppelin/contracts/access/Ownable.sol";

contract AuraResonanceOracle is Ownable {
    struct AuraResonance {
        uint256 tokenId;
        uint256 frequency; // 432 Hz scaled
        uint256 resonanceScore; // 0-1000
        bool aligned;
    }

    mapping(uint256 => AuraResonance) public resonances;
    event ResonanceUpdated(uint256 indexed tokenId, uint256 score, bool aligned);

    function updateResonance(
        uint256 tokenId,
        uint256 frequency,
        uint256 score
    ) external onlyOwner {
        require(score <= 1000, "Score exceeds max");
        bool aligned = score >= 750;
        resonances[tokenId] = AuraResonance(tokenId, frequency, score, aligned);
        emit ResonanceUpdated(tokenId, score, aligned);
    }

    function getResonance(uint256 tokenId) external view returns (AuraResonance memory) {
        return resonances[tokenId];
    }
}
