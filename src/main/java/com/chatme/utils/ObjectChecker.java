package com.chatme.utils;

import java.math.BigDecimal;
import java.util.*;

public class ObjectChecker
{
	/***
	 *  checks whether an object is empty or null
	 * @param object variable of Type Object
	 * @return Boolean with state <Truer> if the object is empty or null</True> <False> if the Object is not Empty or null</False>
	 */
	public static boolean isEmptyOrNull(Object object)
	{
		if (object == null)
			return true;
		if (object instanceof Collection)
			return ((Collection<?>) object).isEmpty();
		if (object instanceof Map)
			return ((Map<?, ?>) object).isEmpty();
		if (object instanceof String)
			return ((String) object).isEmpty();
		if (object instanceof StringBuilder)
			return ((StringBuilder) object).isEmpty();
		return false;
	}

	public static boolean isNotEmptyOrNull(Object object)
	{
		return !isEmptyOrNull(object);
	}

	/***
	 * Compares Between two Variables Ofm Type Object
	 * @param object1 variables Ofm Type Object
	 * @param object2 variables Ofm Type Object
	 * @return Boolean <True> if the Two Objects are equal</True> <False> If the Objects aren't equal</False>
	 */
	public static boolean areEqual(Object object1, Object object2)
	{
		if (object1 == object2)
			return true;
		if (object1 == null)
			return false;
		if (object2 == null)
			return false;
		if (object1 instanceof Enum)
			object1 = object1.toString();
		if (object2 instanceof Enum)
			object2 = object2.toString();
		if (object1 instanceof BigDecimal && object2 instanceof BigDecimal)
			return ((BigDecimal) object1).compareTo((BigDecimal) object2) == 0;
		return object1.equals(object2);
	}

	public static boolean areNotEqual(Object object1, Object object2)
	{
		return !areEqual(object1, object2);
	}

	/***
	 * Checks whether Many Objects are empty or null
	 * @param objects Many Objects seberated By commas like (Object1,Object2)
	 * @return Boolean with state <Truer> if the object is empty or null</True> <False> if the Object is not Empty or null</False>
	 */
	public static boolean isAnyEmptyOrNull(Object... objects)
	{
		for (Object o : objects)
		{
			if (isEmptyOrNull(o))
				return true;
		}
		return false;
	}
}
