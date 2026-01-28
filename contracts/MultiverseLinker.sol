// SPDX-License-Identifier: MIT
pragma solidity ^0.8.23;

import "@openzeppelin/contracts/access/Ownable.sol";

contract MultiverseLinker is Ownable {
    mapping(uint256 => uint256[]) public pastReincarnations;
    mapping(uint256 => uint256[]) public futureReincarnations;
    mapping(uint256 => uint256) public karmaCarryOver;

    event ReincarnationLinked(uint256 indexed current, uint256 indexed previous, bool isPast);

    function linkReincarnation(
        uint256 currentTokenId,
        uint256 linkedTokenId,
        bool isPast,
        uint256 carryOverKarma
    ) external onlyOwner {
        if (isPast) {
            pastReincarnations[currentTokenId].push(linkedTokenId);
        } else {
            futureReincarnations[currentTokenId].push(linkedTokenId);
        }
        karmaCarryOver[currentTokenId] = carryOverKarma;
        emit ReincarnationLinked(currentTokenId, linkedTokenId, isPast);
    }

    function getPastReincarnations(uint256 tokenId) external view returns (uint256[] memory) {
        return pastReincarnations[tokenId];
    }

    function getFutureReincarnations(uint256 tokenId) external view returns (uint256[] memory) {
        return futureReincarnations[tokenId];
    }

    function getKarmaCarryOver(uint256 tokenId) external view returns (uint256) {
        return karmaCarryOver[tokenId];
    }
}
