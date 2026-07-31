package com.musically.studio.ui;

import android.content.Context;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.musically.studio.network.ApiClient;
import com.musically.studio.network.MaveSessionManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<ApiClient> apiClientProvider;

  private final Provider<MaveSessionManager> maveSessionManagerProvider;

  private final Provider<FirebaseAuth> authProvider;

  private final Provider<FirebaseDatabase> rtdbProvider;

  private MainViewModel_Factory(Provider<Context> contextProvider,
      Provider<ApiClient> apiClientProvider,
      Provider<MaveSessionManager> maveSessionManagerProvider, Provider<FirebaseAuth> authProvider,
      Provider<FirebaseDatabase> rtdbProvider) {
    this.contextProvider = contextProvider;
    this.apiClientProvider = apiClientProvider;
    this.maveSessionManagerProvider = maveSessionManagerProvider;
    this.authProvider = authProvider;
    this.rtdbProvider = rtdbProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(contextProvider.get(), apiClientProvider.get(), maveSessionManagerProvider.get(), authProvider.get(), rtdbProvider.get());
  }

  public static MainViewModel_Factory create(Provider<Context> contextProvider,
      Provider<ApiClient> apiClientProvider,
      Provider<MaveSessionManager> maveSessionManagerProvider, Provider<FirebaseAuth> authProvider,
      Provider<FirebaseDatabase> rtdbProvider) {
    return new MainViewModel_Factory(contextProvider, apiClientProvider, maveSessionManagerProvider, authProvider, rtdbProvider);
  }

  public static MainViewModel newInstance(Context context, ApiClient apiClient,
      MaveSessionManager maveSessionManager, FirebaseAuth auth, FirebaseDatabase rtdb) {
    return new MainViewModel(context, apiClient, maveSessionManager, auth, rtdb);
  }
}
