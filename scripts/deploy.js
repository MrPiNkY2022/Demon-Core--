// scripts/deploy.js
// Hardhat deployment script for SoulContract.sol
// Run with: npx hardhat run scripts/deploy.js --network <network-name>
// Example: npx hardhat run scripts/deploy.js --network sepolia
// Prerequisites:
// - Hardhat configured in hardhat.config.js with your network (e.g., sepolia)
// - .env file with PRIVATE_KEY (for signer) and optional ETHERSCAN_API_KEY
// - Install dependencies: npm install --save-dev @nomicfoundation/hardhat-toolbox dotenv
// - Add to hardhat.config.js if not already: require("dotenv").config();

const hre = require("hardhat");

async function main() {
  console.log("🚀 Deploying Demon Core SoulContract... 🔱");
  console.log("This deploys an eternal soulbound NFT contract for metaphysical soul tokens.");

  // Get the contract factory
  const SoulContract = await hre.ethers.getContractFactory("SoulContract");

  // Deploy the contract
  // Constructor sets msg.sender as owner (your deployer wallet)
  const soulContract = await SoulContract.deploy();

  // Wait for deployment transaction to be mined
  await soulContract.waitForDeployment();

  const deployedAddress = await soulContract.getAddress();
  console.log(`\nSoulContract deployed successfully!`);
  console.log(`Contract Address: ${deployedAddress}`);
  console.log(`Owner (you): ${await soulContract.owner()}`);

  // Optional: Mint an initial soul token for testing (as owner)
  console.log("\nMinting genesis soul token for testing...");
  const genesisToAddress = "0xYourTestWalletAddressHere"; // Replace with a test address (e.g., your own)
  const genesisUri = "ipfs://QmYourSoulMetadataJsonHere"; // Replace with actual IPFS URI or placeholder
  const genesisInitialKarma = 42n; // BigInt for int256

  const mintTx = await soulContract.safeMint(genesisToAddress, genesisUri, genesisInitialKarma);
  await mintTx.wait();

  console.log(`Genesis Soul Token minted to ${genesisToAddress}`);
  console.log(`Transaction Hash: ${mintTx.hash}`);
  console.log(`View on explorer: https://sepolia.etherscan.io/tx/${mintTx.hash} (if on sepolia)`);

  // Optional: Verify on Etherscan (if API key in .env)
  if (hre.network.name !== "hardhat" && hre.network.name !== "localhost") {
    console.log("\nVerifying contract on Etherscan...");
    try {
      await hre.run("verify:verify", {
        address: deployedAddress,
        constructorArguments: [], // No constructor args
      });
      console.log("Contract verified successfully!");
    } catch (err) {
      console.error("Verification failed (maybe already verified or no API key):", err.message);
    }
  }

  console.log("\nDeployment complete. Update your backend .env and Android code with the new contract address!");
  console.log("The Demon Core now has eternal souls on-chain. Handle with metaphysical caution. 🔱");
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });

// ... existing main() ...

async function main() {
  // Deploy SoulContract
  const SoulContract = await hre.ethers.getContractFactory("SoulContract");
  const soulContract = await SoulContract.deploy();
  await soulContract.waitForDeployment();
  console.log("SoulContract:", await soulContract.getAddress());

  // Deploy KarmaOracle
  const KarmaOracle = await hre.ethers.getContractFactory("KarmaOracle");
  const karmaOracle = await KarmaOracle.deploy(await soulContract.getAddress());
  await karmaOracle.waitForDeployment();
  console.log("KarmaOracle:", await karmaOracle.getAddress());

  // Deploy ReincarnationRegistry
  const ReincarnationRegistry = await hre.ethers.getContractFactory("ReincarnationRegistry");
  const registry = await ReincarnationRegistry.deploy();
  await registry.waitForDeployment();
  console.log("ReincarnationRegistry:", await registry.getAddress());

  // Mint genesis as before...
}
