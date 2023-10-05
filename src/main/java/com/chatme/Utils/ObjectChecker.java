package com.chatme.Utils;

import java.math.BigDecimal;
import java.util.*;

public class ObjectChecker
{
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
