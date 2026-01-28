// contracts/SoulEvents.sol - Extension or separate contract for event logging
// SPDX-License-Identifier: MIT
pragma solidity ^0.8.4;

contract SoulEvents {
    event SoulMinted(uint256 indexed tokenId, address indexed owner, int256 initialKarma);
    event KarmaUpdated(uint256 indexed tokenId, int256 oldKarma, int256 newKarma);
    event AuraAligned(uint256 indexed tokenId, uint256 auraLevel);

    // Can be called by owner or oracle
    function emitKarmaUpdate(uint256 tokenId, int256 oldKarma, int256 newKarma) external {
        emit KarmaUpdated(tokenId, oldKarma, newKarma);
    }

    // Example usage: Call from SoulContract or backend oracle
}
