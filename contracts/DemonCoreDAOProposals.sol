// SPDX-License-Identifier: MIT
pragma solidity ^0.8.23;

import "./SoulGovernance.sol";

contract DemonCoreDAOProposals is SoulGovernance {
    enum ProposalType {
        ParameterUpdate,
        ContractUpgrade,
        OracleAdd,
        DecayOverride
    }

    mapping(uint256 => ProposalType) public proposalTypes;

    function proposeParameterUpdate(
        string memory description,
        bytes memory calldatas
    ) public returns (uint256) {
        uint256 proposalId = propose(address(this), 0, description, calldatas);
        proposalTypes[proposalId] = ProposalType.ParameterUpdate;
        return proposalId;
    }

    // Add more proposal types as needed
}
