# አዲስ ቢድ (Addis Bid) 🇪🇹
### Modern Zero-Price Telebirr Bidding Platform for Android

![Addis Bid Platform](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue.svg)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-purple.svg)
![Payment](https://img.shields.io/badge/Payment-Telebirr-orange.svg)

**አዲስ ቢድ (Addis Bid)** በኢትዮጵያ ውስጥ የመጀመሪያው ግልጽ፣ ተዓማኒ እና ዘመናዊ የጨረታ እና የስጦታ መድረክ ነው። 
ተጠቃሚዎች ያለምንም የተጋነነ የገበያ ዋጋ ወይም አላስፈላጊ ውድድር በቀጥታ በቴሌብር (Telebirr) ዋሌት ክፍያቸውን አረጋግጠው የሚሳተፉበት እና አድሚኑ በቀላሉ ክፍያዎችን የሚያስተዳድርበት መተግበሪያ ነው።

---

## 🌟 ቁልፍ ባህሪያት (Key Features)

- **ንቁ እና ግልጽ ጨረታዎች**፦ ምንም አይነት የተጋነነ የገበያ ዋጋ ወይም ውድድር የሌለበት ግልጽ መድረክ።
- **የቴሌብር ዋሌት (Telebirr Wallet Integration)**፦ ተጠቃሚዎች የቴሌብር SMS የደረሰኝ ቁጥራቸውን (Transaction ID) በማስገባት በቀጥታ ዋሌታቸው ላይ ገቢ እንዲሆን ማድረግ ይችላሉ።
- **የአድሚን መቆጣጠሪያ ፓነል (Admin Console)**፦
  - የተላኩ የቴሌብር ደረሰኞችን በእውነተኛ ሰዓት አይቶ የማጽደቅ (Approve) ወይም ውድቅ (Reject) የማድረግ ስልጣን።
  - አዳዲስ እቃዎችን በፎቶ፣ ርዕስ እና ማብራሪያ በቀላሉ የመክፈት ችሎታ።
  - የቴሌብር ተቀባይ ስልክ ቁጥር እና አጭር ኮድ (Shortcode) በቀጥታ ከአፑ የመቀየር አሰራር።
- **የእኔ ጨረታዎች እና ሽልማቶች (My Entries & Vouchers)**፦ ተጠቃሚዎች የተሳተፉባቸውን እቃዎች እና ያሸነፏቸውን ልዩ የዲጂታል ቫውቸሮች የሚያዩበት ገጽ።
- **ሁለት ቋንቋዎች (Bilingual Support)**፦ በአማርኛ እና በእንግሊዝኛ ቋንቋዎች በአንድ ክሊክ የሚቀያየር።
- **ዘመናዊ UI/UX**፦ በቅርቡ የወጣው Material Design 3 (M3) እና የቴሌብር ይፋዊ ቀለሞች (ሰማያዊ፣ አረንጓዴ እና ወርቃማ) ውህደት።

---

## 📱 የገጾች መዋቅር (Screen Architecture)

| ገጽ (Screen) | ማብራሪያ (Description) |
|---|---|
| **እቃዎች (Auctions Feed)** | ንቁ እቃዎች፣ የቀረው ሰዓት እና የተሳታፊዎች ብዛት የሚያሳይ |
| **ዋሌት (Wallet)** | የቴሌብር ክፍያ መመሪያ፣ የሂሳብ መጠን እና የክፍያ ታሪክ |
| **የእኔ (My Bids)** | የተሳተፉባቸው እቃዎች እና ያሸነፏቸው ቫውቸሮች |
| **አድሚን (Admin Panel)** | የቴሌብር ደረሰኞች ማረጋገጫ እና አዳዲስ እቃዎችን ማዘጋጃ |
| **መመሪያ (GitHub & Guide)** | የቴሌብር ነጋዴ ፈቃድ እና የማሰማሪያ ሙሉ መመሪያ |

---

## 🛠 የቴክኖሎጂ ዝርዝር (Tech Stack)

- **ቋንቋ (Language)**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel) + Single Source of Truth Repository Pattern
- **State Management**: Kotlin StateFlow & Coroutines
- **Icons**: Google Material Symbols & Extended Icons

---

## 🚀 አፑን በኮምፒውተርዎ ላይ ለማስኬድ (How to Run)

1. ይህንን Repository በኮምፒውተርዎ ላይ Clone ያድርጉ፦
   ```bash
   git clone https://github.com/mahisalim66-rgb/Addis-bid-app.git
   ```
2. **Android Studio** ይክፈቱ እና `Open Project` በማለት የወረደውን ፎልደር ይምረጡ።
3. አንድሮይድ ስቱዲዮ ላይ Gradle Sync ሲጨርስ **Run (▶)** የሚለውን ይጫኑ።

---

## 📞 የቴሌብር ንግድ ፈቃድ እና ግንኙነት (Telebirr Merchant Setup)

ይህንን መተግበሪያ በይፋ ለተጠቃሚዎች ለማቅረብ፦
1. የኦንላይን ንግድ ፈቃድ ያውጡ።
2. በአቅራቢያዎ ወደሚገኝ የኢትዮ ቴሌኮም ቢሮ በመሄድ የ**ቴሌብር ነጋዴ አካውንት (Merchant Account)** ይክፈቱ።
3. ኢትዮ ቴሌኮም የሰጠዎትን የቴሌብር ቁጥር በመተግበሪያው አድሚን ፓነል ውስጥ ያስገቡ።

---

© 2026 Addis Bid. All rights reserved.
