// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.datafascia.common.jackson.DFObjectMapper;
import com.datafascia.reflections.PackageUtils;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Provide schema for the various models available via the server
 */
@Produces(MediaType.APPLICATION_JSON) @Slf4j @Path("/")
public class RootResource {
  /** Package name for models */
  public static final String MODELS_PKG = "com.datafascia.models";

  /**
   * @return the JSON schema for the model
   */
  @GET @Timed @Path("/schema")
  public Set<JsonSchema> schema() {
    Set<JsonSchema> schemas = new HashSet<>();
    for (Class<?> resClass : PackageUtils.classes(MODELS_PKG)) {
      schemas.add(modelSchema(resClass.getName()));
    }

    return schemas;
  }

  /**
   * @param modelName the name of the model
   *
   * @return the JSON schema for the model
   */
  @GET @Timed @Path("/schema/{modelName}")
  public JsonSchema schemaFirst(@PathParam("modelName") String modelName) {
    return modelSchema(MODELS_PKG + "." + modelName);
  }

  /**
   * @param modelName the name of the model
   *
   * @return the JSON schema for the model
   */
  @GET @Timed @Path("/{modelName}/schema")
  public JsonSchema schemaLast(@PathParam("modelName") String modelName) {
    return modelSchema(MODELS_PKG + "." + modelName);
  }

  /**
   * Return the JSON schema associated with class
   *
   * @param modelName the name of the model
   *
   * @return the JSON schema for the model
   */
  private JsonSchema modelSchema(String modelName) throws WebApplicationException {
    try {
      log.info("Fetching schema for " + modelName);
      ObjectMapper mapper = DFObjectMapper.objectMapper();
      SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
      Class<?> modelClass = Class.forName(modelName);
      mapper.acceptJsonFormatVisitor(modelClass, visitor);

      return visitor.finalSchema();
    } catch (JsonMappingException | ClassNotFoundException e) {
      throw new WebApplicationException("Invalid model name.", Response.Status.BAD_REQUEST);
    }
  }
}
