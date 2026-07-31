
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



public interface CreateCameraCaptureMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      CreateCameraCaptureMutation.Data,
      CreateCameraCaptureMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val imageUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val videoUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val environmentData: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val generatedTrackId: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var imageUrl: String?
        public var videoUrl: String?
        public var environmentData: String?
        public var generatedTrackId: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          
          block_: Builder.() -> Unit
        ): Variables {
          var imageUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var videoUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var environmentData: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var generatedTrackId: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var imageUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { imageUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var videoUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { videoUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var environmentData: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { environmentData = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var generatedTrackId: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { generatedTrackId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              imageUrl=imageUrl,videoUrl=videoUrl,environmentData=environmentData,generatedTrackId=generatedTrackId,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val cameraCapture_insert: CameraCaptureKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreateCameraCapture"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateCameraCaptureMutation.ref(
  
    

  
    block_: CreateCameraCaptureMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreateCameraCaptureMutation.Data,
    CreateCameraCaptureMutation.Variables
  > =
  ref(
    
      CreateCameraCaptureMutation.Variables.build(
        
  
    block_
      )
    
  )

public suspend fun CreateCameraCaptureMutation.execute(

  
    
      

  
    block_: CreateCameraCaptureMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CreateCameraCaptureMutation.Data,
    CreateCameraCaptureMutation.Variables
  > =
  ref(
    
      
  
    block_
    
  ).execute()


