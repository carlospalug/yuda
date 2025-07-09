package com.nightroll.app.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ReelRepository_Factory implements Factory<ReelRepository> {
  @Override
  public ReelRepository get() {
    return newInstance();
  }

  public static ReelRepository_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ReelRepository newInstance() {
    return new ReelRepository();
  }

  private static final class InstanceHolder {
    private static final ReelRepository_Factory INSTANCE = new ReelRepository_Factory();
  }
}
