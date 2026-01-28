// SPDX-License-Identifier: MIT
pragma solidity ^0.8.23;

import "@openzeppelin/contracts/access/Ownable.sol";

interface IKarmaConsumer {
    function fulfillKarmaUpdate(uint256 tokenId, int256 newKarma) external;
}

contract KarmaOracleProxy is Ownable {
    IKarmaConsumer public consumer;
    mapping(bytes32 => bool) public pendingRequests;

    event OracleRequest(bytes32 indexed requestId, uint256 tokenId);
    event OracleResponse(bytes32 indexed requestId, int256 karma);

    constructor(address _consumer) Ownable(msg.sender) {
        consumer = IKarmaConsumer(_consumer);
    }

    function requestKarmaUpdate(uint256 tokenId) external onlyOwner returns (bytes32) {
        bytes32 requestId = keccak256(abi.encodePacked(block.timestamp, tokenId));
        pendingRequests[requestId] = true;
        emit OracleRequest(requestId, tokenId);
        return requestId;
    }

    function fulfill(bytes32 requestId, int256 karma) external onlyOwner {
        require(pendingRequests[requestId], "Invalid request");
        delete pendingRequests[requestId];
        consumer.fulfillKarmaUpdate(0, karma); // tokenId from request
        emit OracleResponse(requestId, karma);
    }
}
