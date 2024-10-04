package org.dbs.spring.core.api

import org.springframework.beans.factory.config.ConfigurableBeanFactory

import kotlin.annotation.AnnotationRetention.RUNTIME

@Retention(RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS) //on class level
annotation class RegisteredAsBean(val beanScope: String = ConfigurableBeanFactory.SCOPE_SINGLETON)
