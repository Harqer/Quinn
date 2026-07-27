package com.musically.studio.audio;

import com.musically.studio.network.MaveSessionManager;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class PlaybackService_MembersInjector implements MembersInjector<PlaybackService> {
  private final Provider<MaveSessionManager> maveSessionManagerProvider;

  private PlaybackService_MembersInjector(Provider<MaveSessionManager> maveSessionManagerProvider) {
    this.maveSessionManagerProvider = maveSessionManagerProvider;
  }

  @Override
  public void injectMembers(PlaybackService instance) {
    injectMaveSessionManager(instance, maveSessionManagerProvider.get());
  }

  public static MembersInjector<PlaybackService> create(
      Provider<MaveSessionManager> maveSessionManagerProvider) {
    return new PlaybackService_MembersInjector(maveSessionManagerProvider);
  }

  @InjectedFieldSignature("com.musically.studio.audio.PlaybackService.maveSessionManager")
  public static void injectMaveSessionManager(PlaybackService instance,
      MaveSessionManager maveSessionManager) {
    instance.maveSessionManager = maveSessionManager;
  }
}
