// contracts/ReincarnationRegistry.sol
// SPDX-License-Identifier: MIT
pragma solidity ^0.8.4;

import "@openzeppelin/contracts/access/Ownable.sol";

contract ReincarnationRegistry is Ownable {
    mapping(uint256 => uint256[]) public pastLives; // current tokenId => array of previous tokenIds
    mapping(uint256 => uint256[]) public futureLives; // current => predicted future

    event PastLifeLinked(uint256 current, uint256 past);
    event FutureLifePredicted(uint256 current, uint256 future);

    function linkPastLife(uint256 currentTokenId, uint256 pastTokenId) external onlyOwner {
        pastLives[currentTokenId].push(pastTokenId);
        emit PastLifeLinked(currentTokenId, pastTokenId);
    }

    function predictFutureLife(uint256 currentTokenId, uint256 futureTokenId) external onlyOwner {
        futureLives[currentTokenId].push(futureTokenId);
        emit FutureLifePredicted(currentTokenId, futureTokenId);
    }

    function getPastLives(uint256 tokenId) external view returns (uint256[] memory) {
        return pastLives[tokenId];
    }

    function getFutureLives(uint256 tokenId) external view returns (uint256[] memory) {
        return futureLives[tokenId];
    }
}
