// backend/server.js - Node.js Express server with Ethereum integration
require('dotenv').config();
const express = require('express');
const Web3 = require('web3');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

const web3 = new Web3(process.env.INFURA_URL || 'https://mainnet.infura.io/v3/YOUR_KEY');
const contractAddress = process.env.SOUL_CONTRACT_ADDRESS;
const abi = [ /* Paste ABI from compiled SoulContract here */ ]; // Or load from artifacts
const contract = new web3.eth.Contract(abi, contractAddress);

// Endpoint: Get karma from chain
app.get('/karma/:tokenId', async (req, res) => {
  try {
    const karma = await contract.methods.karmaBalance(req.params.tokenId).call();
    res.json({ karma: karma.toString() });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Endpoint: Mint soul (requires owner private key - for backend only!)
app.post('/mint', async (req, res) => {
  const { to, uri, initialKarma } = req.body;
  // Security: Use a secure wallet or signer - this is demo
  const account = web3.eth.accounts.privateKeyToAccount(process.env.PRIVATE_KEY);
  web3.eth.accounts.wallet.add(account);

  try {
    const tx = contract.methods.safeMint(to, uri, initialKarma);
    const gas = await tx.estimateGas({ from: account.address });
    const receipt = await tx.send({ from: account.address, gas });
    res.json({ success: true, txHash: receipt.transactionHash });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Backend server running on port ${PORT}`));
