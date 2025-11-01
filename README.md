# 🌍 Blockchain Carbon Registry System

A modern Android application for tracking, trading, and retiring carbon credits on a
blockchain-based registry. Built with Jetpack Compose and featuring a beautiful, intuitive UI.

## ✨ Features

### 📊 Dashboard

- **Real-time Statistics**: Track global CO₂ offset, active projects, and available credits
- **Featured Projects**: Browse top carbon offset projects worldwide
- **User Portfolio**: Monitor your owned and retired carbon credits
- **Modern Material Design**: Clean, accessible interface with smooth animations

### 🌱 Carbon Projects

- **Diverse Project Types**:
    - 🌳 Reforestation
    - ☀️ Renewable Energy (Solar & Wind)
    - ⚡ Energy Efficiency
    - 🏭 Methane Capture
    - 🌊 Ocean Conservation
    - 💨 Carbon Capture & Storage
    - 🌾 Sustainable Agriculture

- **Search & Filter**: Find projects by name, location, or type
- **Detailed Information**: View impact metrics, verification standards, and documentation
- **Geographic Data**: Track project locations with coordinates

### 💳 Carbon Credits

- **Credit Management**: Buy, transfer, and retire carbon credits
- **Blockchain Verification**: Every credit is verified on the blockchain
- **Status Tracking**: Monitor credit status (Active, Retired, Pending, etc.)
- **Detailed Metadata**: Serial numbers, vintage year, methodology, and certification bodies

### 🔐 Wallet

- **Secure Blockchain Wallet**: Auto-generated cryptographic wallet
- **Transaction History**: Complete audit trail of all operations
- **Portfolio Summary**: View owned and retired credits
- **Blockchain Addresses**: Full transparency with wallet addresses

## 🏗️ Architecture

### Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Blockchain**: Web3j-compatible implementation
- **Async Operations**: Kotlin Coroutines & Flow
- **Navigation**: Compose Navigation
- **Cryptography**: Native SHA-256 hashing

### Project Structure

```
app/src/main/java/com/runanywhere/startup_hackathon20/
├── data/                           # Data models
│   └── CarbonCredit.kt             # Credit, Project, Transaction models
├── blockchain/                     # Blockchain layer
│   └── BlockchainService.kt        # Wallet & transaction management
├── repository/                     # Data repository
│   └── CarbonRepository.kt         # Mock data & business logic
├── viewmodel/                      # ViewModels
│   └── CarbonViewModel.kt          # UI state management
└── MainActivity.kt                 # UI screens & components
```

## 🚀 Quick Start

### Prerequisites

- Android Studio Arctic Fox or newer
- Android SDK 24 (Android 7.0) or higher
- Kotlin 1.9+
- Gradle 8.0+

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/Nishat2006/carbonchainapp.git
cd carbonchainapp
```

2. **Open in Android Studio**
    - Open Android Studio
    - Select "Open an existing project"
    - Navigate to the project directory

3. **Sync Gradle**
    - Android Studio will automatically sync Gradle dependencies
    - Wait for the sync to complete

4. **Run the app**
    - Connect an Android device or start an emulator
    - Click the "Run" button or press `Shift + F10`

### First Launch

When you first launch the app:

1. **Wallet Creation**: A blockchain wallet is automatically created
2. **Browse Dashboard**: View global impact statistics
3. **Explore Projects**: Browse 5 pre-loaded carbon offset projects
4. **View Credits**: See 4 sample carbon credits available
5. **Purchase Credits**: Buy credits to add to your wallet
6. **Retire Credits**: Permanently remove credits from circulation

## 📱 App Screens

### 1. Dashboard

- Global impact overview
- Featured projects carousel
- Recent credits list
- Statistics cards (CO₂ offset, active projects, credits)

### 2. Projects

- Search bar for finding projects
- Filter by project type (chips)
- List of all carbon offset projects
- Detailed project cards with:
    - Project type icon and color
    - Location and description
    - Impact metrics (CO₂ reduced, trees planted, etc.)
    - Available vs. retired credits
    - Verification standard

### 3. Credits

- Filter by credit status
- Detailed credit cards showing:
    - Serial number
    - Amount in tonnes CO₂
    - Price per tonne
    - Vintage year
    - Blockchain hash
- Buy button for active credits
- Retire button with dialog
- Verification on blockchain

### 4. Wallet

- Wallet address display
- Credits owned & retired summary
- Transaction history
- Create wallet button (if not exists)
- Transaction cards with:
    - Transaction type (issuance, transfer, retirement)
    - Timestamp and block number
    - Transaction hash
    - Amount and status

## 🔐 Blockchain Features

### Wallet Management

- **Auto-generation**: Secure random private key generation
- **Address**: SHA-256 derived Ethereum-style addresses (0x...)
- **Security**: Private keys stored securely (in production, use Android Keystore)

### Transaction Types

1. **Issuance**: Creating new carbon credits on the blockchain
2. **Transfer**: Moving credits between addresses
3. **Retirement**: Permanently removing credits from circulation
4. **Verification**: Blockchain proof of credit authenticity

### Blockchain Simulation

Currently, the app simulates blockchain operations for demonstration:

- Transaction hashing with SHA-256
- Simulated block numbers and gas fees
- Realistic transaction confirmation delays
- Blockchain proof generation

**For Production**: Replace `BlockchainService` with real blockchain integration:

- Ethereum/Polygon smart contracts
- IPFS for document storage
- Real Web3j provider connection

## 📊 Sample Data

The app comes with 5 pre-loaded carbon offset projects:

| Project                        | Type               | Location | CO₂ Offset |
|--------------------------------|--------------------|----------|------------|
| Amazon Rainforest Conservation | Reforestation      | Brazil   | 150,000 t  |
| Solar Farm India               | Renewable Energy   | India    | 250,000 t  |
| Wind Energy Denmark            | Renewable Energy   | Denmark  | 320,000 t  |
| Mangrove Restoration Vietnam   | Ocean Conservation | Vietnam  | 85,000 t   |
| Methane Capture Dairy Farm     | Methane Capture    | USA      | 45,000 t   |

## 🎨 UI/UX Highlights

- **Material 3 Design**: Modern Google Material Design components
- **Color-coded Projects**: Each project type has a unique color scheme
- **Smooth Animations**: Fade-ins, slides, and loading states
- **Responsive Layout**: Works on phones and tablets
- **Dark Mode Support**: System theme-aware (built into Material 3)
- **Accessibility**: Proper content descriptions and contrast ratios

## 🔧 Configuration

### Dependencies

Key dependencies are defined in `app/build.gradle.kts`:

- **Blockchain**: Web3j Core (4.9.8)
- **Cryptography**: BouncyCastle Provider
- **UI**: Jetpack Compose with Material 3
- **Navigation**: Compose Navigation
- **DataStore**: For preferences

### Building for Production

1. **Update blockchain configuration** in `BlockchainService.kt`:
   ```kotlin
   // Replace with actual blockchain network
   private val networkId = "mainnet" // or testnet
   ```

2. **Add real API endpoints** in `CarbonRepository.kt`

3. **Implement secure storage** for private keys using Android Keystore

4. **Add real verification** services (Verra, Gold Standard, etc.)

5. **Configure ProGuard** rules for release builds

## 🌐 Future Enhancements

- [ ] Real blockchain integration (Ethereum/Polygon)
- [ ] Smart contract deployment
- [ ] IPFS document storage
- [ ] QR code scanning for wallet addresses
- [ ] Market price charts for carbon credits
- [ ] Push notifications for transactions
- [ ] Multi-wallet support
- [ ] Carbon calculator integration
- [ ] Social sharing of retired credits
- [ ] NFT certificates for retirement
- [ ] Multi-language support
- [ ] Export transaction reports (PDF/CSV)

## 🧪 Testing

### Manual Testing

1. Launch app and verify wallet creation
2. Browse projects and credits
3. Purchase a credit (simulated)
4. Retire a credit
5. Verify transaction appears in wallet
6. Check blockchain hash format

### Unit Testing

(To be implemented)

```kotlin
// Example test structure
class BlockchainServiceTest {
    @Test
    fun testWalletGeneration() {
        // Test implementation
    }

    @Test
    fun testTransactionHashing() {
        // Test implementation
    }
}
```

## 📄 License

This project is created for demonstration purposes. For production use, ensure compliance with:

- Carbon credit standards (Verra, Gold Standard, etc.)
- Blockchain regulations in your jurisdiction
- Data privacy laws (GDPR, CCPA, etc.)

## 🤝 Contributing

Contributions are welcome! Areas for improvement:

- Real blockchain integration
- Additional project types
- Enhanced UI components
- Test coverage
- Documentation

## 📧 Support

For issues or questions:

1. Check existing GitHub issues
2. Create a new issue with detailed description
3. Include screenshots for UI issues
4. Provide logcat output for crashes

## 🙏 Acknowledgments

- **Material Design**: Google's design system
- **Jetpack Compose**: Modern Android UI toolkit
- **Web3j**: Ethereum blockchain library
- **Carbon Standards**: Verra, Gold Standard, Plan Vivo, Climate Action Reserve

---
Built with ❤️ for a sustainable future 🌱
