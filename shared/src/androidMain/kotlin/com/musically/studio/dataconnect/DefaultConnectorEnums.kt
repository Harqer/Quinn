
@file:Suppress(
  "KotlinRedundantDiagnosticSuppress",
  "PropertyName",
  "MayBeConstant",
  "RedundantVisibilityModifier",
  "RedundantCompanionReference",
  "RemoveEmptyClassBody",
  "SpellCheckingInspection",
  "unused",
)

package com.musically.studio.dataconnect


  
  
  public enum class CategoryType {
  GENRE,
  MOOD,
  ACTIVITY;
  
  
    public object EnumValueSerializer :
      com.musically.studio.dataconnect.EnumValueSerializer<CategoryType>(CategoryType.entries)
  
  }

  
  
  public enum class SubscriptionTier {
  FREE,
  PREMIUM,
  FAMILY;
  
  
    public object EnumValueSerializer :
      com.musically.studio.dataconnect.EnumValueSerializer<SubscriptionTier>(SubscriptionTier.entries)
  
  }

