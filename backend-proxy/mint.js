const express = require('express');
const ethers = require('ethers');
const cors = require('cors');
require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());

const provider = new ethers.JsonRpcProvider(process.env.INFURA_URL);
const wallet = new ethers.Wallet(process.env.PRIVATE_KEY, provider);
const contract = new ethers.Contract(
    process.env.SOUL_CONTRACT_ADDRESS,
    ['function safeMint(address to, string uri, int256 initialKarma) external'],
    wallet
);

app.post('/mint', async (req, res) => {
    const { to, uri, initialKarma } = req.body;

    try {
        const tx = await contract.safeMint(to, uri, initialKarma);
        const receipt = await tx.wait();
        res.json({ success: true, txHash: receipt.hash });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.listen(3001, () => console.log('Proxy running on 3001'));
