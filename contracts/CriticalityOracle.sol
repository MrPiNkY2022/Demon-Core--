// SPDX-License-Identifier: MIT
pragma solidity ^0.8.23;

import "@openzeppelin/contracts/access/Ownable.sol";

contract CriticalityOracle is Ownable {
    uint256 public constant CRITICAL_THRESHOLD = 9200; // 92.00%
    mapping(uint256 => uint256) public criticalityScores;

    event CriticalityTriggered(uint256 indexed tokenId, uint256 score);

    function updateCriticality(uint256 tokenId, uint256 score) external onlyOwner {
        require(score <= 10000, "Score too high");
        criticalityScores[tokenId] = score;
        if (score >= CRITICAL_THRESHOLD) {
            emit CriticalityTriggered(tokenId, score);
        }
    }

    function isCritical(uint256 tokenId) external view returns (bool) {
        return criticalityScores[tokenId] >= CRITICAL_THRESHOLD;
    }
}
