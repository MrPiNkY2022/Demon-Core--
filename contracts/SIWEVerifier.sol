// SPDX-License-Identifier: MIT
pragma solidity ^0.8.23;

import "@openzeppelin/contracts/utils/cryptography/ECDSA.sol";
import "@openzeppelin/contracts/utils/cryptography/MessageHashUtils.sol";

library SIWEVerifier {
    using ECDSA for bytes32;
    using MessageHashUtils for bytes32;

    // Minimal SIWE message parser + verifier
    function verify(
        address expectedSigner,
        string calldata message,
        bytes calldata signature
    ) internal pure returns (bool) {
        bytes32 ethSignedMessageHash = keccak256(
            abi.encodePacked("\x19Ethereum Signed Message:\n", bytes(message).length, message)
        );

        address recovered = ethSignedMessageHash.recover(signature);
        return recovered == expectedSigner;
    }
}
