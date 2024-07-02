package com.slgames.store.utils;

import com.slgames.store.dtos.DTO;

public interface BodyObjectTest {

	public abstract DTO createdDTO();
	public abstract DTO expectedCreatedDTO();
	public abstract DTO expectedDTO();
	public abstract DTO updatedDTO();
	public abstract DTO updatedDTOWithInvalidID();
}
