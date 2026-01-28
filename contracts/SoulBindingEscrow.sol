// SPDX-License-Identifier: MIT
pragma solidity ^0.8.23;

import "@openzeppelin/contracts/token/ERC721/IERC721.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

contract SoulBindingEscrow is Ownable {
    IERC721 public soulToken;
    mapping(uint256 => address) public lockedOwner;
    mapping(uint256 => uint256) public unlockTimestamp;

    event SoulLocked(uint256 indexed tokenId, address owner, uint256 unlockTime);
    event SoulUnlocked(uint256 indexed tokenId, address to);

    constructor(address _soulToken) Ownable(msg.sender) {
        soulToken = IERC721(_soulToken);
    }

    function lockSoul(uint256 tokenId, uint256 lockDuration) external {
        require(msg.sender == soulToken.ownerOf(tokenId), "Not owner");
        soulToken.transferFrom(msg.sender, address(this), tokenId);
        lockedOwner[tokenId] = msg.sender;
        unlockTimestamp[tokenId] = block.timestamp + lockDuration;
        emit SoulLocked(tokenId, msg.sender, unlockTimestamp[tokenId]);
    }

    function unlockSoul(uint256 tokenId) external {
        require(lockedOwner[tokenId] == msg.sender, "Not locked owner");
        require(block.timestamp >= unlockTimestamp[tokenId], "Locked");
        soulToken.transferFrom(address(this), msg.sender, tokenId);
        delete lockedOwner[tokenId];
        delete unlockTimestamp[tokenId];
        emit SoulUnlocked(tokenId, msg.sender);
    }
}
