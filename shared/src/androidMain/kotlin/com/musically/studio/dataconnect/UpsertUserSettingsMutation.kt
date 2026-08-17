
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



public interface UpsertUserSettingsMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      UpsertUserSettingsMutation.Data,
      UpsertUserSettingsMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val theme: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val parentalControlsEnabled: com.google.firebase.dataconnect.OptionalVariable<Boolean?>,
  
    val notificationsEnabled: com.google.firebase.dataconnect.OptionalVariable<Boolean?>,
  
    val appsDevicesEnabled: com.google.firebase.dataconnect.OptionalVariable<Boolean?>,
  
    val offlineMode: com.google.firebase.dataconnect.OptionalVariable<Boolean?>,
  
    val stripeCustomerId: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var theme: String?
        public var parentalControlsEnabled: Boolean?
        public var notificationsEnabled: Boolean?
        public var appsDevicesEnabled: Boolean?
        public var offlineMode: Boolean?
        public var stripeCustomerId: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          
          block_: Builder.() -> Unit
        ): Variables {
          var theme: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var parentalControlsEnabled: com.google.firebase.dataconnect.OptionalVariable<Boolean?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var notificationsEnabled: com.google.firebase.dataconnect.OptionalVariable<Boolean?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var appsDevicesEnabled: com.google.firebase.dataconnect.OptionalVariable<Boolean?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var offlineMode: com.google.firebase.dataconnect.OptionalVariable<Boolean?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var stripeCustomerId: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var theme: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { theme = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var parentalControlsEnabled: Boolean?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { parentalControlsEnabled = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var notificationsEnabled: Boolean?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { notificationsEnabled = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var appsDevicesEnabled: Boolean?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { appsDevicesEnabled = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var offlineMode: Boolean?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { offlineMode = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var stripeCustomerId: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { stripeCustomerId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              theme=theme,parentalControlsEnabled=parentalControlsEnabled,notificationsEnabled=notificationsEnabled,appsDevicesEnabled=appsDevicesEnabled,offlineMode=offlineMode,stripeCustomerId=stripeCustomerId,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val userSettings_upsert: UserSettingsKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "UpsertUserSettings"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpsertUserSettingsMutation.ref(
  
    

  
    block_: UpsertUserSettingsMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    UpsertUserSettingsMutation.Data,
    UpsertUserSettingsMutation.Variables
  > =
  ref(
    
      UpsertUserSettingsMutation.Variables.build(
        
  
    block_
      )
    
  )

public suspend fun UpsertUserSettingsMutation.execute(

  
    
      

  
    block_: UpsertUserSettingsMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    UpsertUserSettingsMutation.Data,
    UpsertUserSettingsMutation.Variables
  > =
  ref(
    
      
  
    block_
    
  ).execute()


