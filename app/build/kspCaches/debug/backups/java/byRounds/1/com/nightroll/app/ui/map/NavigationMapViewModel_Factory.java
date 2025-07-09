package com.nightroll.app.ui.map;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.nightroll.app.data.repository.PlacesRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class NavigationMapViewModel_Factory implements Factory<NavigationMapViewModel> {
  private final Provider<PlacesRepository> placesRepositoryProvider;

  private final Provider<FusedLocationProviderClient> fusedLocationClientProvider;

  public NavigationMapViewModel_Factory(Provider<PlacesRepository> placesRepositoryProvider,
      Provider<FusedLocationProviderClient> fusedLocationClientProvider) {
    this.placesRepositoryProvider = placesRepositoryProvider;
    this.fusedLocationClientProvider = fusedLocationClientProvider;
  }

  @Override
  public NavigationMapViewModel get() {
    return newInstance(placesRepositoryProvider.get(), fusedLocationClientProvider.get());
  }

  public static NavigationMapViewModel_Factory create(
      Provider<PlacesRepository> placesRepositoryProvider,
      Provider<FusedLocationProviderClient> fusedLocationClientProvider) {
    return new NavigationMapViewModel_Factory(placesRepositoryProvider, fusedLocationClientProvider);
  }

  public static NavigationMapViewModel newInstance(PlacesRepository placesRepository,
      FusedLocationProviderClient fusedLocationClient) {
    return new NavigationMapViewModel(placesRepository, fusedLocationClient);
  }
}
