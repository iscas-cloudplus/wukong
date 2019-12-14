/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.wukong.qingcloud_vm;

import io.github.wukong.core.KindParameterAnalyzer;
import io.github.wukong.core.gens.ParamterStyleGenerator;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @since 2019/5/8
 * 
 */
public class QingcloudVMKindParameterAnalyzerTest {

	public static void main(String[] args) {
		QingcloudVMKindAnalyzer ka = new QingcloudVMKindAnalyzer();
		KindParameterAnalyzer mpa = new KindParameterAnalyzer(ka);
		for (String kind : ka.getKinds()) {
			ParamterStyleGenerator.showModelParametersWithJsonStyle(
					kind, mpa.getParameters(kind));
		}
	}

}
