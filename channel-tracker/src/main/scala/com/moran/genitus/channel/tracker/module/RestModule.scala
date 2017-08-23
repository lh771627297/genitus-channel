package com.moran.genitus.channel.tracker.module

import javax.inject.Named

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.google.inject.{Provides, Scopes, Singleton}
import com.moran.genitus.channel.tracker.router.RestApiRouter


class RestModule extends org.genitus.cardiac.rest.RestModule{

  protected override def configureServlets() {
    super.configureServlets()
    bind(classOf[RestApiRouter]).in(Scopes.SINGLETON)
  }


  /**
    * Provides rest mapper.
    *
    * @return rest mapper.
    */
  @Provides
  @Singleton
  @Named("restMapper")
  def provideRestMapper(): ObjectMapper = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    mapper
  }
}
