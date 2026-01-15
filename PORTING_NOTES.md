# Starcatcher Port to NeoForge 1.21.11 - Progress Update

## Summary
- **Starting errors**: ~200
- **Current errors**: ~100
- **Main remaining**: Recipe System, Entity/Model System

---

## COMPLETED FIXES

### 1. libtooltips Library
- Ported to 1.21.11
- Fixed ResourceLocation -> Identifier
- BUILD SUCCESSFUL

### 2. Rendering Changes (DONE)
- `guiGraphics.blit()` now requires `RenderPipelines.GUI_TEXTURED` as first parameter
- `guiGraphics.pose()` returns `Matrix3x2fStack` instead of `PoseStack`
  - `pushPose()` -> `pushMatrix()`
  - `popPose()` -> `popMatrix()`
  - `translate(x, y, z)` -> `translate(x, y)` (2D only)
- `RenderSystem.setShaderColor/enableBlend/disableBlend` removed - use blit color parameter
- Created `ARGB.colorFromFloat(a, r, g, b)` for color construction

### 3. Network Changes (DONE)
- `PacketDistributor.sendToServer()` removed
- Use `minecraft.getConnection().send(payload)` instead

### 4. Input Changes (DONE)
- `Screen.hasShiftDown()` no longer static
- Use `InputConstants.isKeyDown(window, InputConstants.KEY_LSHIFT)`

### 5. Widget Changes (DONE)
- `AbstractWidget.mouseClicked()` signature changed
- Use `onClick(double mouseX, double mouseY)` without @Override

### 6. Particle System (DONE)
- `TextureSheetParticle` removed - use `SingleQuadParticle` instead
- Constructor requires `TextureAtlasSprite`: `super(level, x, y, z, spriteSet.get(0, 1))`
- `getRenderType()` replaced with `getLayer()` returning `SingleQuadParticle.Layer`
  - Use `SingleQuadParticle.Layer.TRANSLUCENT` or `SingleQuadParticle.Layer.OPAQUE`
- `ParticleProvider.createParticle()` now has `RandomSource` parameter

### 7. Slot API (DONE)
- `Slot.getNoItemIcon()` now returns `Identifier` instead of `Pair<Identifier, Identifier>`
- Just return the sprite location directly

### 8. RenderType Methods (DONE)
- `RenderType.entityCutout(Identifier)` moved to `RenderTypes.entityCutout(Identifier)`
- Import `net.minecraft.client.renderer.rendertype.RenderTypes`

### 9. Registry Access (DONE)
- `registryAccess().registryOrThrow()` renamed to `registryAccess().lookupOrThrow()`

---

## REMAINING ISSUES (~100 errors)

### 1. RECIPE SYSTEM (Major rewrite needed - ~50 errors)

**Files:**
- `ModifierShapedRecipe.java`
- `ModifierShapelessRecipe.java`
- `FishingRodSmithingRecipe.java`

**Key changes needed:**
- `getSerializer()` return type: `RecipeSerializer<? extends CraftingRecipe>`
- `getType()` return type: `RecipeType<SmithingRecipe>` (not wildcard)
- `getIngredients()` returns `List<Optional<Ingredient>>` not `NonNullList<Ingredient>`
- `SmithingRecipe` requires `additionIngredient()` method
- `Ingredient.getItems()` removed - use new Ingredient API
- Many `@Override` methods no longer exist in parent interfaces

### 2. ENTITY/MODEL SYSTEM (~20 errors)

**FishingBobModel.java:**
- `renderToBuffer()` is final in parent - cannot override
- `root()` is final in parent - cannot override
- Need to extend different class or use composition

**FishingBobEntity.java:**
- `moveTo(double, double, double, float, float)` removed
- `kill()` signature changed - takes `ServerLevel` parameter
- Some override methods no longer valid

**FishingBobRenderer.java:**
- `extractRenderState()` method signature changed
- `render()` method signature changed
- `RenderType.lineStrip()` removed

### 3. UTILITY (~5 errors)

**TrustedHolder.java:**
- `createStandAlone()` return type incompatible with parent

**Network Payloads:**
- StreamCodec method references invalid
- `GameProfile` codec changed

---

## Files Modified This Session
- FishingBitingParticles.java - new particle API
- FishingBitingLavaParticles.java - new particle API
- FishingNotificationParticles.java - new particle API
- FishingRodMenu.java - Slot.getNoItemIcon() return type
- AbstractTackleSkin.java - RenderTypes.entityCutout()
- ActiveSweetSpot.java - lookupOrThrow()

## Notes
- libtooltips builds successfully and is ready
- Particle system fully ported
- Recipe/Entity systems need major rewrites - consider simplifying or using vanilla types
