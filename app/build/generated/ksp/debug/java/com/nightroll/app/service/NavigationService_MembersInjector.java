package com.nightroll.app.service;

import com.google.android.gms.location.FusedLocationProviderClient;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
    "deprecation"
})
public final class NavigationService_MembersInjector implements MembersInjector<NavigationService> {
  private final Provider<FusedLocationProviderClient> fusedLocationClientProvider;

  public NavigationService_MembersInjector(
      Provider<FusedLocationProviderClient> fusedLocationClientProvider) {
    this.fusedLocationClientProvider = fusedLocationClientProvider;
  }

  public static MembersInjector<NavigationService> create(
      Provider<FusedLocationProviderClient> fusedLocationClientProvider) {
    return new NavigationService_MembersInjector(fusedLocationClientProvider);
  }

  @Override
  public void injectMembers(NavigationService instance) {
    injectFusedLocationClient(instance, fusedLocationClientProvider.get());
  }

  @InjectedFieldSignature("com.nightroll.app.service.NavigationService.fusedLocationClient")
  public static void injectFusedLocationClient(NavigationService instance,
      FusedLocationProviderClient fusedLocationClient) {
    instance.fusedLocationClient = fusedLocationClient;
  }
}
