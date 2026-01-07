# PNG Icon Implementation Guide

## Quick Setup Using Android Studio Asset Studio (Recommended)

### Step 1: Prepare Your PNG
1. Export from Figma as PNG:
   - Size: 1024×1024 pixels (minimum) or 2048×2048 (preferred)
   - Format: PNG with transparency
   - Save to: `design/App Icon.png`

### Step 2: Use Android Studio Asset Studio
1. In Android Studio, right-click on `app/src/main/res` folder
2. Select: **New → Image Asset**
3. In the Asset Studio window:
   - **Icon Type**: Select "Launcher Icons (Adaptive and Legacy)"
   - **Foreground Layer**:
     - Select "Image" tab
     - Click folder icon and browse to your PNG file
     - Adjust scaling/positioning if needed
   - **Background Layer**:
     - Select "Color" tab
     - Choose white (#FFFFFF) or your preferred background color
   - **Legacy Icon**: ✅ Check "Generate PNG legacy icons"
   - **Round Icon**: ✅ Check "Generate round icon"
4. Click **Next** → **Finish**
5. Android Studio will automatically:
   - Generate all required PNG sizes
   - Create adaptive icon XML files
   - Place files in correct folders
   - Update AndroidManifest.xml if needed

### Step 3: Verify
- Check that files were created in:
  - `app/src/main/res/mipmap-*/ic_launcher.png` (all densities)
  - `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` (adaptive icon)
- Build and test the app

## Manual Setup (Alternative)

If you prefer manual setup or Asset Studio doesn't work:

1. **Resize PNG to required sizes:**
   - mdpi: 48×48px
   - hdpi: 72×72px
   - xhdpi: 96×96px
   - xxhdpi: 144×144px
   - xxxhdpi: 192×192px

2. **Place files in:**
   - `app/src/main/res/mipmap-mdpi/ic_launcher.png`
   - `app/src/main/res/mipmap-hdpi/ic_launcher.png`
   - `app/src/main/res/mipmap-xhdpi/ic_launcher.png`
   - `app/src/main/res/mipmap-xxhdpi/ic_launcher.png`
   - `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png`

3. **Update adaptive icon XML** to reference PNG instead of vector drawable

## Current Status

The project is currently set up for vector drawables. Once you provide the PNG file, I can:
1. Update the adaptive icon structure to use PNGs
2. Generate all required sizes (if you provide a high-res source)
3. Replace the vector drawable implementation

