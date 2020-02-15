package org.iii.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimplePBEConfig;
import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.Fraction;

import static org.apache.commons.lang3.exception.ExceptionUtils.*;

public class CommonUtils
{
	private static Logger logger = LoggerFactory.getLogger(CommonUtils.class);

	// Array utils =============================================================
	
	public static <T> T elementAt(T[] array, int idx){
		if(array != null){
			int len = array.length;
			if(idx < 0) idx += len;
			if( 0 <= idx && idx < len)
				return array[idx];
		}
		return null;
	}

	public static <T> T elementAt(Collection<T> c, int idx){
		if(c != null){
			int len = c.size();
			if(idx < 0) idx += len;
			if( 0 <= idx && idx < len){
				
				if(c instanceof List){
					return ((List<T>)c).get(idx);
				}
				else{
					Iterator<T> it = c.iterator();
					for(int i = 0; i < idx; ++i)
						it.next();
					return it.next();
				}
			}
		}
		return null;
	}

	public static <T> T elementAt(List<T> list, int idx){
		if(list != null){
			int len = list.size();
			if(idx < 0) idx += len;
			if( 0 <= idx && idx < len){
				return ((List<T>)list).get(idx);
			}
		}
		return null;

	}

	public static <T> T elementLast(T[] array){
		return elementAt(array, -1);
	}

	public static <T> T elementLast(List<T> list){
		return elementAt(list, -1);
	}

	public static <T> T elementLast(Collection<T> c){
		return elementAt(c, -1);
	}



	public static <T> boolean isEmpty(Collection<T> c){
		return c == null || c.isEmpty();
	}

	public static <K,V> boolean isEmpty(Map<K,V> m){
		return m == null || m.isEmpty();
	}

	public static <T> boolean isEmpty(T[] array){
		return array == null || array.length == 0;
	}

	//==================================
	public static List<JSONObject> tolist(JSONArray arr){
		if(arr == null || arr.length() == 0)
			return Collections.emptyList();
		
		List<JSONObject> result = new ArrayList<>(arr.length());
		arr.forEach(obj->result.add((JSONObject)obj));
		return result;
	}

	//alias name for string equals
	public static boolean streql(CharSequence cs1, CharSequence cs2){
		return StringUtils.equals(cs1, cs2);
	}
	
	public static boolean equalAny(Object obj, Object...objs){
		for(Object o: objs)
			if(o.equals(obj))
				return true;
		return false;
	}

}

