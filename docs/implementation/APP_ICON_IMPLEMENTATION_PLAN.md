# App Icon Implementation Plan

## Overview

Design and implement a custom app icon for Rewire that reflects the app's purpose as a habit and addiction tracker. The icon should be modern, recognizable, and work well across all Android device types and screen densities. The implementation will use Android's Adaptive Icon system (Android 8.0+) for modern devices while maintaining backward compatibility.

## Current State

- App currently uses default Android Studio generated launcher icons
- Adaptive icon infrastructure is already in place:
  - `ic_launcher.xml` and `ic_launcher_round.xml` in `mipmap-anydpi-v26/`
  - Background drawable: `ic_launcher_background.xml`
  - Foreground drawable: `ic_launcher_foreground.xml`
- AndroidManifest.xml references `@mipmap/ic_launcher` and `@mipmap/ic_launcher_round`
- Icon currently shows a generic Android robot head design

## Design Requirements

### Visual Design Considerations

1. **App Identity**:
   - Should represent "rewiring" habits or behavior change
   - Could incorporate visual metaphors: circular patterns (habit cycles), connections/networks, growth/progress
   - Should align with app theme colors (check AppColors in theme files)

2. **Adaptive Icon Requirements**:
   - **Safe Zone**: Core content must fit within 66dp × 66dp central area
   - **Total Size**: 108dp × 108dp canvas (foreground + background)
   - **Mask Shape**: System may apply circular, square, rounded square, or squircle masks
   - Design must look good in all mask shapes

3. **Icon Characteristics**:
   - Simple and recognizable at small sizes (24dp, 48dp)
   - High contrast for visibility on various backgrounds
   - Minimal text (or no text)
   - Works in both light and dark themes

4. **Color Considerations**:
   - Should match or complement app theme colors
   - Consider how the icon appears on various launcher backgrounds
   - Ensure sufficient contrast for accessibility

### Design Specifications

- **Recommended Tools**: 
  - Figma (free, web-based) or Adobe Illustrator (professional, paid)
  - Vector graphics preferred for scalability
  
- **Design Canvas Size**: 
  - Start with 1024px × 1024px (or 512px × 512px minimum)
  - Vector format recommended (SVG)

- **Safe Zone Guide**:
  - Draw a 512px × 512px safe zone in the center (for 1024px canvas)
  - Keep essential elements within this zone
  - Decorative elements can extend to edges but may be masked

## File Format Specifications

### Required Files and Formats

#### 1. Source Design Files
- **Format**: SVG (vector) or high-resolution PNG (2048px × 2048px minimum)
- **Location**: Design files (not committed to repo, archive separately)
- **Recommended**: Create both foreground and background as separate layers/files

#### 2. Adaptive Icon Components (Android 8.0+)

##### Background Layer
- **File**: `app/src/main/res/drawable/ic_launcher_background.xml`
- **Format**: Vector Drawable (XML) or 9-patch PNG
- **Size**: 108dp × 108dp effective size
- **Purpose**: Static background color/gradient that shows behind the foreground
- **Guidelines**:
  - Solid color or simple gradient
  - Should complement foreground design
  - Must extend to all edges (no transparency needed at edges)

##### Foreground Layer  
- **File**: `app/src/main/res/drawable/ic_launcher_foreground.xml`
- **Format**: Vector Drawable (XML) preferred, or PNG
- **Size**: 108dp × 108dp canvas, but content in safe zone (72dp × 72dp central area)
- **Purpose**: Main icon graphic that appears on top of background
- **Guidelines**:
  - Main visual elements should fit in central 72dp × 72dp safe zone
  - Can extend to edges for decorative elements
  - Transparent background (background layer shows through)

##### Monochrome Layer (Optional but Recommended)
- **File**: `app/src/main/res/drawable/ic_launcher_monochrome.xml`
- **Format**: Vector Drawable (XML)
- **Purpose**: Simplified monochrome version for themed icons (Android 13+)
- **Guidelines**:
  - Black/white or single-color silhouette
  - Must be recognizable in monochrome

#### 3. Adaptive Icon XML Files

##### Regular Icon
- **File**: `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`
- **Format**: XML adaptive icon definition
- **Content**: References background and foreground drawables

##### Round Icon
- **File**: `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`  
- **Format**: XML adaptive icon definition
- **Content**: Same references as regular icon (can use same files)

#### 4. Legacy PNG Icons (Android 7.1 and below)

For devices running Android 7.1 and below, PNG files are needed in multiple densities:

- **Location**: `app/src/main/res/mipmap-[density]/ic_launcher.png`
- **Densities Required**:
  - `mipmap-mdpi`: 48px × 48px
  - `mipmap-hdpi`: 72px × 72px  
  - `mipmap-xhdpi`: 96px × 96px
  - `mipmap-xxhdpi`: 144px × 144px
  - `mipmap-xxxhdpi`: 192px × 192px

- **Round Icon PNGs** (optional but recommended):
  - `mipmap-[density]/ic_launcher_round.png`
  - Same sizes as above

### File Format Integration Guide

#### Step 1: Design Phase
1. Create icon design in Figma/Illustrator at 1024px × 1024px
2. Design with separate layers for:
   - Background (solid color/gradient)
   - Foreground (main icon graphic)
   - Optional: Monochrome version

#### Step 2: Export for Android

##### For Vector Drawables (Recommended):
1. **Background**: Export as SVG or design as solid color rectangle
   - Convert to XML using Android Studio's Vector Asset Studio
   - Or manually create XML for solid color/gradient

2. **Foreground**: Export main icon as SVG
   - Import via Android Studio: `File > New > Vector Asset`
   - Select "Local File (SVG, PSD)" and choose your SVG
   - Adjust size and path if needed
   - Save as `ic_launcher_foreground.xml`

##### For PNG Assets:
1. Export at multiple resolutions (see legacy PNG sizes above)
2. Use Android Asset Studio (online tool) to generate all required sizes
3. Or manually export at each density

#### Step 3: Generate Adaptive Icons

If using Android Studio:
1. Right-click `res` folder → `New` → `Image Asset`
2. Select "Launcher Icons (Adaptive and Legacy)"
3. Configure:
   - **Foreground Layer**: Select your foreground image (PNG/SVG/XML)
   - **Background Layer**: Select background (color/image)
   - **Legacy Icon**: Check "Generate PNG legacy icons"
   - **Round Icon**: Check "Generate round icon"
4. Click "Next" and "Finish"
5. Android Studio will generate all required files automatically

#### Step 4: Manual Integration (Alternative)

If not using Android Studio's asset generator:

1. **Background XML** (`ic_launcher_background.xml`):
```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#YOUR_COLOR"
        android:pathData="M0,0h108v108h-108z"/>
</vector>
```

2. **Foreground XML** (`ic_launcher_foreground.xml`):
   - Import SVG via Vector Asset Studio
   - Or create manually using path data

3. **Adaptive Icon XML** (`mipmap-anydpi-v26/ic_launcher.xml`):
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
```

4. **Legacy PNGs**: Place in respective `mipmap-[density]/` folders

## Implementation Steps

### Phase 1: Design Creation
- [ ] Create icon design concept
- [ ] Design foreground layer (main icon graphic)
- [ ] Design background layer (color/gradient)
- [ ] Create monochrome version (optional)
- [ ] Test design in various mask shapes (circle, square, rounded)
- [ ] Verify visibility at small sizes

### Phase 2: Asset Preparation
- [ ] Export foreground as SVG or high-res PNG
- [ ] Export background as SVG or define as color
- [ ] Export monochrome version (if created)
- [ ] Verify all assets meet size requirements
- [ ] Ensure safe zone guidelines are followed

### Phase 3: Asset Integration
- [ ] Generate vector drawables from SVG assets
- [ ] Create/update `ic_launcher_background.xml`
- [ ] Create/update `ic_launcher_foreground.xml`
- [ ] Create `ic_launcher_monochrome.xml` (optional)
- [ ] Update `mipmap-anydpi-v26/ic_launcher.xml` if needed
- [ ] Update `mipmap-anydpi-v26/ic_launcher_round.xml` if needed
- [ ] Generate legacy PNG icons for all densities
- [ ] Place PNG files in correct mipmap folders

### Phase 4: Testing and Validation
- [ ] Test on Android 8.0+ devices (adaptive icons)
- [ ] Test on Android 7.1 and below (legacy PNG icons)
- [ ] Verify icon appears correctly on device home screen
- [ ] Test icon in app drawer
- [ ] Verify icon in recent apps view
- [ ] Test icon in different launcher apps
- [ ] Verify icon on various background colors
- [ ] Test icon in light and dark themes
- [ ] Verify round icon variant (if applicable)
- [ ] Check icon at various sizes (small, medium, large)
- [ ] Validate icon on different screen densities

### Phase 5: Polish and Finalization
- [ ] Make adjustments based on testing feedback
- [ ] Optimize file sizes if needed
- [ ] Document design decisions
- [ ] Archive source design files

## Testing Considerations

### Device Testing
1. **Android Versions**:
   - Test on Android 7.1 and below (legacy PNG icons)
   - Test on Android 8.0-12 (adaptive icons)
   - Test on Android 13+ (themed icons/monochrome support)

2. **Screen Densities**:
   - mdpi (160dpi)
   - hdpi (240dpi)
   - xhdpi (320dpi)
   - xxhdpi (480dpi)
   - xxxhdpi (640dpi)

3. **Launcher Variations**:
   - Default Android launcher
   - Third-party launchers (Nova, Microsoft Launcher, etc.)
   - Different icon shape preferences (circular, square, etc.)

### Visual Validation Checklist
- [ ] Icon is recognizable at 24dp size
- [ ] Icon is clear and visible at 48dp size
- [ ] Icon looks good in circular mask
- [ ] Icon looks good in square mask
- [ ] Icon looks good in rounded square mask
- [ ] Icon maintains visual balance when masked
- [ ] Colors work on light backgrounds
- [ ] Colors work on dark backgrounds
- [ ] Sufficient contrast for accessibility
- [ ] No pixelation or blur at any size

## Design Tool Recommendations

### Professional Tools
1. **Figma** (Recommended - Free)
   - Web-based, collaborative
   - Easy SVG export
   - Built-in Android icon templates
   - Free tier available

2. **Adobe Illustrator** (Premium - Paid)
   - Industry standard
   - Advanced vector editing
   - Professional workflow

3. **Affinity Designer** (One-time Purchase)
   - Professional alternative to Illustrator
   - One-time purchase (no subscription)

### Specialized Tools
4. **IconKitchen** (Free - Web)
   - Online Android icon generator
   - Can generate all required sizes automatically
   - Upload your design and generate assets

5. **Android Asset Studio** (Free - Web/Android Studio)
   - Built into Android Studio
   - Generates all required files automatically
   - Can convert SVG to XML

### Free Alternatives
6. **GIMP** (Free - Desktop)
   - Raster graphics editor
   - Good for PNG creation
   - Steeper learning curve

7. **Inkscape** (Free - Desktop)
   - Vector graphics editor
   - SVG native format
   - Open source

## Files to Update

### New Files to Create
1. Design source files (archived, not in repo):
   - Icon design file (Figma/Illustrator/etc.)
   - Export files (SVG, PNG)

2. Updated drawable files:
   - `app/src/main/res/drawable/ic_launcher_background.xml` (replace existing)
   - `app/src/main/res/drawable/ic_launcher_foreground.xml` (replace existing)
   - `app/src/main/res/drawable/ic_launcher_monochrome.xml` (new, optional)

3. Legacy PNG icons (if not auto-generated):
   - `app/src/main/res/mipmap-mdpi/ic_launcher.png`
   - `app/src/main/res/mipmap-hdpi/ic_launcher.png`
   - `app/src/main/res/mipmap-xhdpi/ic_launcher.png`
   - `app/src/main/res/mipmap-xxhdpi/ic_launcher.png`
   - `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png`
   - Round icon variants (optional)

### Files to Update (if needed)
1. `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` (verify references)
2. `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml` (verify references)
3. `app/src/main/AndroidManifest.xml` (should already be correct, verify)

## Quick Reference: File Format Requirements

| File Type | Location | Format | Size/Resolution | Required |
|-----------|----------|--------|-----------------|----------|
| Foreground | `drawable/ic_launcher_foreground.xml` | Vector XML | 108dp × 108dp | Yes |
| Background | `drawable/ic_launcher_background.xml` | Vector XML | 108dp × 108dp | Yes |
| Monochrome | `drawable/ic_launcher_monochrome.xml` | Vector XML | 108dp × 108dp | Optional |
| Adaptive (regular) | `mipmap-anydpi-v26/ic_launcher.xml` | XML | N/A | Yes |
| Adaptive (round) | `mipmap-anydpi-v26/ic_launcher_round.xml` | XML | N/A | Yes |
| Legacy (mdpi) | `mipmap-mdpi/ic_launcher.png` | PNG | 48px × 48px | Yes* |
| Legacy (hdpi) | `mipmap-hdpi/ic_launcher.png` | PNG | 72px × 72px | Yes* |
| Legacy (xhdpi) | `mipmap-xhdpi/ic_launcher.png` | PNG | 96px × 96px | Yes* |
| Legacy (xxhdpi) | `mipmap-xxhdpi/ic_launcher.png` | PNG | 144px × 144px | Yes* |
| Legacy (xxxhdpi) | `mipmap-xxxhdpi/ic_launcher.png` | PNG | 192px × 192px | Yes* |

*Required for Android 7.1 and below. Android Studio Asset Studio can auto-generate these.

## Status

- [ ] Planning phase
- [ ] Design finalized
- [ ] Assets created
- [ ] Assets integrated
- [ ] Testing completed
- [ ] Documentation updated

---

*This document will be expanded with specific design decisions, color choices, and design rationale once the icon design is finalized.*

