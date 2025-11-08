# Fintech Spender App

A modern fintech mobile application built with **Jetpack Compose** featuring a beautiful *
*purple-themed UI** and comprehensive financial management features.

## Features

### 🎨 Design

- **Purple-themed UI** with modern Material Design 3 components
- **Gradient backgrounds** and smooth animations
- **Card-based layout** for better information hierarchy
- **Responsive design** that works across different screen sizes

### 💰 Financial Features

- **Dashboard Overview** with total balance display
- **Account Management** with multiple account types
- **Transaction History** with categorized transactions
- **Quick Actions** for common financial operations
- **Regret Log** to track and reflect on past spendings.
- **User Profile** management

### 🏗️ Technical Features

- Built with **Jetpack Compose** for modern Android UI
- **Material Design 3** components and theming
- **Custom purple color scheme** throughout the app
- **Bottom navigation** with 4 main sections
- **Lazy loading** for efficient scrolling
- **State management** with Compose state

## App Structure

### Navigation Tabs

1. **Home** - Dashboard with balance, quick actions, and recent transactions
2. **Accounts** - View and manage different financial accounts
3. **Regret Log** - A log of past spendings that the user regrets, to encourage better spending habits.
4. **Profile** - User settings and account information

### Color Scheme

- **Primary Purple**: `#6A1B9A`
- **Deep Purple**: `#4A148C`
- **Light Purple**: `#E1BEE7`
- **Accent Colors**: Success green, warning orange, error red

## Screenshots

The app features:

- Welcome card with personalized greeting
- Balance display with growth indicators
- Quick action buttons for common tasks
- Recent transactions with icons and amounts
- Account overview with balances
- Regret log section to reflect on spending.
- User profile with avatar

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 24+
- Kotlin 1.8+

### Installation

1. Clone the repository
2. Open in Android Studio
3. Build and run on device or emulator

### Dependencies

- Jetpack Compose BOM
- Material Design 3
- Material Icons Extended
- Activity Compose
- Lifecycle Runtime

## Architecture

The app follows modern Android development practices:

- **MVVM pattern** ready for implementation
- **Compose-first** UI development
- **Material Design 3** theming system
- **Modular structure** for easy maintenance

## Customization

The purple theme can be easily customized by modifying the colors in:

```kotlin
app/src/main/java/com/example/fintechspender/ui/theme/Color.kt
```

## Future Enhancements

- Integration with real banking APIs
- Biometric authentication
- Push notifications for transactions
- Expense categorization and budgeting
- Dark mode support
- Multi-language support

## License

This project is open source and available under the MIT License.
