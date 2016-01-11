package com.grachro.muddler;


import spark.Request;

public class ViewParameter extends HashMap {

	public ViewParameter(Request request) {

		for (String queryParam : request.queryParams()) {
			String[] ss = request.queryParamsValues(queryParam);
			if (ss == null) {
				continue;
			}

			if (ss.length == 1) {
				this.put(queryParam, ss[0]);
			} else {
				this.put(queryParam, ss);
			}

		}

	}

}
