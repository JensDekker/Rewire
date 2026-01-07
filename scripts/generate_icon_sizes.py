#!/usr/bin/env python3
"""
Script to generate Android icon sizes from a source PNG.
Requires Pillow: pip install Pillow
"""

import os
import sys
from PIL import Image

# Icon sizes for each density
SIZES = {
    'mdpi': 48,
    'hdpi': 72,
    'xhdpi': 96,
    'xxhdpi': 144,
    'xxxhdpi': 192,
}

# Adaptive icon foreground size (108dp = 432px for xxxhdpi)
ADAPTIVE_FOREGROUND_SIZE = 432

def resize_icon(source_path, output_path, size, quality=95):
    """Resize an image to the specified size."""
    try:
        img = Image.open(source_path)
        
        # Convert RGBA if needed
        if img.mode != 'RGBA':
            img = img.convert('RGBA')
        
        # Resize with high-quality resampling
        resized = img.resize((size, size), Image.Resampling.LANCZOS)
        
        # Save as PNG
        resized.save(output_path, 'PNG', optimize=True)
        print(f"[OK] Generated {output_path} ({size}x{size})")
        return True
    except Exception as e:
        print(f"[ERROR] Error generating {output_path}: {e}")
        return False

def main():
    # Paths
    project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    source_png = os.path.join(project_root, 'design', 'App Icon.png')
    app_res = os.path.join(project_root, 'app', 'src', 'main', 'res')
    
    # Check if source exists
    if not os.path.exists(source_png):
        print(f"Error: Source PNG not found at {source_png}")
        sys.exit(1)
    
    print(f"Source PNG: {source_png}")
    print(f"Output directory: {app_res}\n")
    
    # Check source image size
    source_img = Image.open(source_png)
    print(f"Source image size: {source_img.size[0]}x{source_img.size[1]}\n")
    
    # Generate legacy icons
    print("Generating legacy icons...")
    for density, size in SIZES.items():
        output_dir = os.path.join(app_res, f'mipmap-{density}')
        output_path = os.path.join(output_dir, 'ic_launcher.png')
        
        if not os.path.exists(output_dir):
            os.makedirs(output_dir)
        
        resize_icon(source_png, output_path, size)
    
    # Generate adaptive icon foreground (432px for xxxhdpi)
    print("\nGenerating adaptive icon foreground...")
    for density in ['mdpi', 'hdpi', 'xhdpi', 'xxhdpi', 'xxxhdpi']:
        # Calculate size: 108dp scaled for each density
        dp_to_px = {
            'mdpi': 48,    # 108dp * 0.75 (mdpi scale 0.75x)
            'hdpi': 72,    # 108dp * 1.0 (hdpi scale 1x)
            'xhdpi': 144,  # 108dp * 1.5 (xhdpi scale 1.5x)
            'xxhdpi': 216, # 108dp * 2.0 (xxhdpi scale 2x)
            'xxxhdpi': 288 # 108dp * 3.0 (xxxhdpi scale 3x) - actually should be 432 for true xxxhdpi
        }
        
        # For adaptive icons, we need larger sizes
        adaptive_sizes = {
            'mdpi': 81,     # 108dp * 0.75
            'hdpi': 108,    # 108dp * 1.0
            'xhdpi': 162,   # 108dp * 1.5
            'xxhdpi': 216,  # 108dp * 2.0
            'xxxhdpi': 432  # 108dp * 4.0 (xxxhdpi is 4x, not 3x for adaptive icons)
        }
        
        size = adaptive_sizes.get(density, dp_to_px.get(density, 192))
        output_dir = os.path.join(app_res, f'mipmap-{density}')
        output_path = os.path.join(output_dir, 'ic_launcher_foreground.png')
        
        if not os.path.exists(output_dir):
            os.makedirs(output_dir)
        
        resize_icon(source_png, output_path, size)
    
    print("\n[SUCCESS] All icon sizes generated successfully!")
    print("\nNext steps:")
    print("1. Verify the icons look correct")
    print("2. Build and test the app")
    print("3. Check the icon in the app launcher")

if __name__ == '__main__':
    try:
        main()
    except ImportError:
        print("Error: Pillow library not found.")
        print("Install it with: pip install Pillow")
        sys.exit(1)

