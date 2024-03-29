package io.ceze.regulus.control.service;

import io.ceze.regulus.generator.model.Disposal;

public record CollectionEvent(CollectionEventType type, Disposal disposal) { }
