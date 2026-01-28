// contracts/KarmaOracle.sol
// SPDX-License-Identifier: MIT
pragma solidity ^0.8.4;

import "@openzeppelin/contracts/access/Ownable.sol";

contract KarmaOracle is Ownable {
    address public soulContract;

    event KarmaUpdateRequested(uint256 tokenId, int256 proposedKarma);

    constructor(address _soulContract) Ownable(msg.sender) {
        soulContract = _soulContract;
    }

    // Oracle (backend) calls this to propose update
    function requestKarmaUpdate(uint256 tokenId, int256 proposedKarma) external onlyOwner {
        // In prod, add verification (e.g., signatures)
        emit KarmaUpdateRequested(tokenId, proposedKarma);
        // SoulContract can listen or be called to update
    }

    // Optional: Direct update if trusted
    function updateKarmaDirect(uint256 tokenId, int256 newKarma) external onlyOwner {
        (bool success, ) = soulContract.call(
            abi.encodeWithSignature("updateKarma(uint256,int256)", tokenId, newKarma)
        );
        require(success, "Update successful");
    }
}
