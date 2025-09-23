package com.example.rewire.ui.theme;

/**
 * Centralized padding and spacing definitions for consistent UI layout across the app
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u000e\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0007\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0006R\u0019\u0010\t\u001a\u00020\n\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\r\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u000e\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0006R\u0011\u0010\u0010\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0006R\u0019\u0010\u0012\u001a\u00020\n\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\r\u001a\u0004\b\u0013\u0010\fR\u0019\u0010\u0014\u001a\u00020\n\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\r\u001a\u0004\b\u0015\u0010\fR\u0019\u0010\u0016\u001a\u00020\n\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\r\u001a\u0004\b\u0017\u0010\f\u0082\u0002\u000b\n\u0005\b\u00a1\u001e0\u0001\n\u0002\b!\u00a8\u0006\u0018"}, d2 = {"Lcom/example/rewire/ui/theme/AppSpacing;", "", "()V", "cardPadding", "Landroidx/compose/foundation/layout/PaddingValues;", "getCardPadding", "()Landroidx/compose/foundation/layout/PaddingValues;", "largePadding", "getLargePadding", "largeSpacing", "Landroidx/compose/ui/unit/Dp;", "getLargeSpacing-D9Ej5fM", "()F", "F", "modalPadding", "getModalPadding", "smallPadding", "getSmallPadding", "smallSpacing", "getSmallSpacing-D9Ej5fM", "standardRowHeight", "getStandardRowHeight-D9Ej5fM", "standardSpacing", "getStandardSpacing-D9Ej5fM", "app_debug"})
public final class AppSpacing {
    
    /**
     * Standard card padding used for habit cards, add buttons, and other main UI elements
     * - 11dp horizontal, 8dp vertical for consistent card spacing
     */
    @org.jetbrains.annotations.NotNull()
    private static final androidx.compose.foundation.layout.PaddingValues cardPadding = null;
    
    /**
     * Modal padding for dialog and modal content
     * - 16dp all around for comfortable modal spacing
     */
    @org.jetbrains.annotations.NotNull()
    private static final androidx.compose.foundation.layout.PaddingValues modalPadding = null;
    
    /**
     * Small padding for compact elements
     * - 8dp all around for tight spacing
     */
    @org.jetbrains.annotations.NotNull()
    private static final androidx.compose.foundation.layout.PaddingValues smallPadding = null;
    
    /**
     * Large padding for prominent elements
     * - 24dp all around for spacious layouts
     */
    @org.jetbrains.annotations.NotNull()
    private static final androidx.compose.foundation.layout.PaddingValues largePadding = null;
    
    /**
     * Standard spacing between elements
     * - 16dp for consistent element separation
     */
    private static final float standardSpacing = 0.0F;
    
    /**
     * Small spacing for tight layouts
     * - 8dp for compact element separation
     */
    private static final float smallSpacing = 0.0F;
    
    /**
     * Large spacing for spacious layouts
     * - 24dp for generous element separation
     */
    private static final float largeSpacing = 0.0F;
    
    /**
     * Standard row height for consistent component sizing
     * - 64dp for habit cards and similar components
     */
    private static final float standardRowHeight = 0.0F;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.rewire.ui.theme.AppSpacing INSTANCE = null;
    
    private AppSpacing() {
        super();
    }
    
    /**
     * Standard card padding used for habit cards, add buttons, and other main UI elements
     * - 11dp horizontal, 8dp vertical for consistent card spacing
     */
    @org.jetbrains.annotations.NotNull()
    public final androidx.compose.foundation.layout.PaddingValues getCardPadding() {
        return null;
    }
    
    /**
     * Modal padding for dialog and modal content
     * - 16dp all around for comfortable modal spacing
     */
    @org.jetbrains.annotations.NotNull()
    public final androidx.compose.foundation.layout.PaddingValues getModalPadding() {
        return null;
    }
    
    /**
     * Small padding for compact elements
     * - 8dp all around for tight spacing
     */
    @org.jetbrains.annotations.NotNull()
    public final androidx.compose.foundation.layout.PaddingValues getSmallPadding() {
        return null;
    }
    
    /**
     * Large padding for prominent elements
     * - 24dp all around for spacious layouts
     */
    @org.jetbrains.annotations.NotNull()
    public final androidx.compose.foundation.layout.PaddingValues getLargePadding() {
        return null;
    }
}