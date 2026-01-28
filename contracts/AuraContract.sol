// contracts/AuraContract.sol - Dedicated soulbound contract for aura levels and colors
// SPDX-License-Identifier: MIT
pragma solidity ^0.8.4;

import "@openzeppelin/contracts/token/ERC721/ERC721.sol";
import "@openzeppelin/contracts/token/ERC721/extensions/ERC721URIStorage.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

contract AuraContract is ERC721, ERC721URIStorage, Ownable {
    uint256 private _tokenIdCounter;

    mapping(uint256 => uint256) public auraLevel; // 0-100 scale
    mapping(uint256 => string) public auraColor; // e.g., "Violet", "Crimson"

    constructor() ERC721("DemonCoreAura", "DCA") Ownable(msg.sender) {}

    function mintAura(address to, string memory uri, uint256 level, string memory color) public onlyOwner {
        _tokenIdCounter += 1;
        uint256 tokenId = _tokenIdCounter;
        _safeMint(to, tokenId);
        _setTokenURI(tokenId, uri);
        auraLevel[tokenId] = level;
        auraColor[tokenId] = color;
    }

    // Non-transferable aura (soul essence bound)
    function _beforeTokenTransfer(address from, address to, uint256 tokenId) internal override {
        require(from == address(0), "Aura is eternally bound to the soul");
        super._beforeTokenTransfer(from, to, tokenId);
    }

    function _burn(uint256 tokenId) internal override(ERC721, ERC721URIStorage) {
        super._burn(tokenId);
    }

    function tokenURI(uint256 tokenId) public view override(ERC721, ERC721URIStorage) returns (string memory) {
        return super.tokenURI(tokenId);
    }

    function updateAura(uint256 tokenId, uint256 newLevel, string memory newColor) public onlyOwner {
        require(_ownerOf(tokenId) != address(0), "Aura token not minted");
        auraLevel[tokenId] = newLevel;
        auraColor[tokenId] = newColor;
    }
}
