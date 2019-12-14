/**
 * Copyrigt (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.cloudpluslab.wukong.discover.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.fabric8.kubernetes.api.model.ObjectMeta;

/**
 * @author tangting18@otcaix.iscas.ac.cn
 * @author wuheng@iscas.ac.cn
 * @since 2019.12.10
 */
public class JSONUtils {

	public static String metaInfo(String kind) {
		return "{\r\n" + "  \"apiVersion\": \"apiextensions.k8s.io/v1beta1\",\r\n"
				+ "  \"kind\": \"CustomResourceDefinition\",\r\n" + "  \"metadata\": {\r\n" + "    \"name\": \""
				+ kind.toLowerCase() + "s.cloudplus.io\"\r\n" + "  },\r\n" + "  \"spec\": {\r\n"
				+ "    \"additionalPrinterColumns\": [\r\n" + "      {\r\n"
				+ "        \"JSONPath\": \".metadata.creationTimestamp\",\r\n" + "        \"name\": \"AGE\",\r\n"
				+ "        \"type\": \"date\"\r\n" + "      }\r\n" + "    ],\r\n"
				+ "    \"group\": \"cloudplus.io\",\r\n" + "    \"names\": {\r\n" + "      \"kind\": \"" + kind
				+ "\",\r\n" + "      \"plural\": \"" + kind.toLowerCase() + "s\",\r\n" + "      \"shortNames\": [\r\n"
				+ "        \"" + kind.toLowerCase() + "\"\r\n" + "      ],\r\n" + "      \"singular\": \""
				+ kind.toLowerCase() + "\"\r\n" + "    },\r\n" + "    \"scope\": \"Namespaced\",\r\n"
				+ "    \"version\": \"v1alpha3\"\r\n" + "  }\r\n" + "}";
	}

	public static String objInfo(String kind, Class<?> cls) throws Exception {
		JSONObject obj = new JSONObject();
		obj.put("apiVersion", "cloudplus.io/v1alpha3");
		obj.put("kind", kind);
		ObjectMeta om = new ObjectMeta();
		om.setName(cls.getSimpleName().toLowerCase());
		obj.put("metadata", om);
		JSONObject spec = new JSONObject();
		spec.put("data", objInfo(cls));
		obj.put("spec", spec);
		return JSON.toJSONString(obj, true);
	}

	public static String xmlInfo(String kind, Method method) throws Exception {
		JSONObject obj = new JSONObject();
		obj.put("apiVersion", "cloudplus.io/v1alpha3");
		obj.put("kind", kind);
		ObjectMeta om = new ObjectMeta();
		om.setName(method.getName().toLowerCase());
		obj.put("metadata", om);
		JSONObject spec = new JSONObject();
		JSONObject jo = new JSONObject();
		for (Parameter param : method.getParameters()) {
			jo.put("_" + param.getName(), xmlInfo(param.getType()));
		}
		spec.put("data", jo);
		obj.put("spec", spec);
		return JSON.toJSONString(obj, true);
	}

	public static Object xmlInfo(Class<?> cls) throws Exception {
		String type = cls.getTypeName();
		if (JavaUtils.isPrimitive(type)) {
			return type;
		} else if (JavaUtils.isStringList(type) || JavaUtils.isStringSet(type)) {
			List<String> list = new ArrayList<String>();
			list.add(String.class.getName());
			list.add(String.class.getName());
			return list;
		} else if (JavaUtils.isStringStringMap(type)) {
			Map<String, String> map = new HashMap<String, String>();
			map.put(String.class.getName() + "0", String.class.getName());
			map.put(String.class.getName() + "1", String.class.getName());
			return map;
		} else if (JavaUtils.isObjectList(type) || JavaUtils.isObjectSet(type)) {

			int sidx = type.indexOf("<");
			int eidx = type.indexOf(">");

			try {
				String newObjClass = type.substring(sidx + 1, eidx);
				List<Object> list = new ArrayList<Object>();
				list.add(objInfo(Class.forName(newObjClass)));
				list.add(objInfo(Class.forName(newObjClass)));
				return list;
			} catch (Exception ex) {
				// ignore here
			}

		} else if (JavaUtils.isStringObjectMap(type)) {

			int sidx = type.indexOf("<");
			int eidx = type.indexOf(",");

			try {
				String newObjClass = type.substring(sidx + 1, eidx);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(String.class.getName() + "0", objInfo(Class.forName(newObjClass)));
				map.put(String.class.getName() + "1", objInfo(Class.forName(newObjClass)));
				return map;
			} catch (Exception ex) {
				// ignore here
			}

		} else {
			try {
				return objInfo(Class.forName(type));
			} catch (Exception ex) {
				// ignore here
			}
		}
		return type;
	}

	public static JSONObject objInfo(Class<?> cls) throws Exception {

		JSONObject obj = new JSONObject();

		for (Field field : cls.getDeclaredFields()) {

			if ((!Modifier.isPrivate(field.getModifiers()) && !Modifier.isProtected(field.getModifiers()))
					|| Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			String type = field.getType().getTypeName();
			String key = field.getName();

			if (JavaUtils.isPrimitive(type)) {
				obj.put(key, type);
			} else if (JavaUtils.isStringList(type) || JavaUtils.isStringSet(type)) {
				List<String> list = new ArrayList<String>();
				list.add(String.class.getName());
				list.add(String.class.getName());
				obj.put(key, list);
			} else if (JavaUtils.isStringStringMap(type)) {
				Map<String, String> map = new HashMap<String, String>();
				map.put(String.class.getName() + "0", String.class.getName());
				map.put(String.class.getName() + "1", String.class.getName());
				obj.put(key, map);
			} else if (JavaUtils.isObjectList(type) || JavaUtils.isObjectSet(type)) {

				int sidx = type.indexOf("<");
				int eidx = type.indexOf(">");

				try {
					String newObjClass = type.substring(sidx + 1, eidx);
					List<Object> list = new ArrayList<Object>();
					list.add(objInfo(Class.forName(newObjClass)));
					list.add(objInfo(Class.forName(newObjClass)));
					obj.put(key, list);
				} catch (Exception ex) {
					// ignore here
					continue;
				}

			} else if (JavaUtils.isStringObjectMap(type)) {

				int sidx = type.indexOf("<");
				int eidx = type.indexOf(",");

				try {
					String newObjClass = type.substring(sidx + 1, eidx);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(String.class.getName() + "0", objInfo(Class.forName(newObjClass)));
					map.put(String.class.getName() + "1", objInfo(Class.forName(newObjClass)));
					obj.put(key, map);
				} catch (Exception ex) {
					// ignore here
					continue;
				}

			} else {
				
				if (type.contains("java.lang")) {
					continue;
				}
				
				try {
					obj.put(key, objInfo(Class.forName(type)));
				} catch (Exception ex) {
					// ignore here
				}
			}
		}

		return obj;
	}

}
