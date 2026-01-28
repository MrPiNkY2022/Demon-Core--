// contracts/SoulContract.sol - Soulbound NFT for metaphysical soul tokens (non-transferable)
// Inspired by soulbound tokens - once minted to a soul, cannot be transferred

// SPDX-License-Identifier: MIT
pragma solidity ^0.8.4;

import "@openzeppelin/contracts/token/ERC721/ERC721.sol";
import "@openzeppelin/contracts/token/ERC721/extensions/ERC721URIStorage.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

contract SoulContract is ERC721, ERC721URIStorage, Ownable {
    uint256 private _tokenIdCounter;

    // Metaphysical data stored on-chain (karma, aura level, etc.)
    mapping(uint256 => string) private _soulMetadata; // JSON or URI to soul data
    mapping(uint256 => int256) public karmaBalance; // Signed karma value

    constructor() ERC721("DemonCoreSoul", "DCS") Ownable(msg.sender) {}

    // Mint a new soul token - only owner (app backend) can mint
    function safeMint(address to, string memory uri, int256 initialKarma) public onlyOwner {
        _tokenIdCounter += 1;
        uint256 tokenId = _tokenIdCounter;
        _safeMint(to, tokenId);
        _setTokenURI(tokenId, uri);
        karmaBalance[tokenId] = initialKarma;
        _soulMetadata[tokenId] = uri; // Can store more data
    }

    // Override to make soulbound - non-transferable
    function _beforeTokenTransfer(address from, address to, uint256 tokenId) internal override {
        require(from == address(0), "Soul tokens are bound eternally - cannot transfer");
        super._beforeTokenTransfer(from, to, tokenId);
    }

    // Burn (release soul?) - optional
    function _burn(uint256 tokenId) internal override(ERC721, ERC721URIStorage) {
        super._burn(tokenId);
    }

    function tokenURI(uint256 tokenId) public view override(ERC721, ERC721URIStorage) returns (string memory) {
        return super.tokenURI(tokenId);
    }

    // Advanced: Update karma (only owner or via oracle)
    function updateKarma(uint256 tokenId, int256 newKarma) public onlyOwner {
        require(_ownerOf(tokenId) != address(0), "Soul not minted");
        karmaBalance[tokenId] = newKarma;
    }
}
