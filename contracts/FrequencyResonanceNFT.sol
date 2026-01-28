// SPDX-License-Identifier: MIT
pragma solidity ^0.8.23;

import "@openzeppelin/contracts/token/ERC721/extensions/ERC721URIStorage.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

contract FrequencyResonanceNFT is ERC721URIStorage, Ownable {
    uint256 private _tokenIdCounter;

    mapping(uint256 => uint256) public frequencyHz;

    constructor() ERC721("FrequencyResonance", "FRNFT") Ownable(msg.sender) {}

    function mintWithFrequency(address to, string memory uri, uint256 hz) public onlyOwner {
        _tokenIdCounter++;
        uint256 tokenId = _tokenIdCounter;
        _safeMint(to, tokenId);
        _setTokenURI(tokenId, uri);
        frequencyHz[tokenId] = hz;
    }

    function getFrequency(uint256 tokenId) external view returns (uint256) {
        return frequencyHz[tokenId];
    }
}
