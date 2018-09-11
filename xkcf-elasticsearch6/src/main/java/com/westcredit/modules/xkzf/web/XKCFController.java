package com.westcredit.modules.xkzf.web;


import com.google.gson.Gson;
import com.westcredit.common.BaseResultAPI;
import com.westcredit.modules.xkzf.entity.QueryResult;
import com.westcredit.modules.xkzf.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;

@RestController
@RequestMapping("/api/v1/xkcf")
public class XKCFController {


    /**
     * 搜索服务
     */
    @Autowired
    public SearchService search;


    /**
     * 根据条件查询行政许可
     *
     * @param xk_xdr_mc
     *            许可相对人名称
     * @param xk_wsh
     *            许可文书号
     * @param xk_xkmc
     *            许可名称
     * @param areacode
     *            地市编码
     * @param xk_xzjg
     *            许可机构
     * @return
     */
    @RequestMapping(value = "/xk/search/condation", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getXK(String xk_xdr_mc, String xk_wsh, String xk_xkmc,
                        String areacode, String areatype, String xk_xzjg,
                        String basecredit, Integer pagesize, Integer pagenum) {
        BaseResultAPI result = new BaseResultAPI();
        try {
            if (pagesize == null) {
                pagesize = 10;
            }
            if (pagenum == null) {
                pagenum = 1;
            }

            QueryResult qresult = search.getXK(basecredit, xk_xdr_mc, xk_wsh,
                    xk_xkmc, areacode, areatype, xk_xzjg, null, pagesize,
                    pagenum, null);
            if (qresult != null) {
                result.setCount(qresult.getCount() == null ? 0 : (qresult
                        .getCount().intValue() > 10000 ? 10000 : qresult
                        .getCount().intValue()));
                result.setDatas(qresult.getResult());
            }
            result.setResultCode(BaseResultAPI.SUCCESS_CODE);
        } catch (Exception e) {
            result.setResultCode(BaseResultAPI.ERROR_CODE);
            result.setResultInfo(BaseResultAPI.ERROR_MSG);
            e.printStackTrace();
        }
        return new Gson().toJson(result);
    }


    /**
     * 行政许可查询详情
     *
     * @return
     */
    @RequestMapping(value = "/xk/search/detial", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getXKDetial(String basecredit, String id) {
        BaseResultAPI result = new BaseResultAPI();
        try {
            QueryResult qresult = search.getXK(basecredit, null, null, null,
                    null, null, null, id, 1, 1, null);
            if (qresult != null) {
                result.setCount(qresult.getCount() == null ? 0 : qresult
                        .getCount().intValue());
                result.setDatas(qresult.getResult());
            }
            result.setResultCode(BaseResultAPI.SUCCESS_CODE);
        } catch (Exception e) {
            result.setResultCode(BaseResultAPI.ERROR_CODE);
            result.setResultInfo(BaseResultAPI.ERROR_MSG);
            e.printStackTrace();
        }
        return new  Gson().toJson(result);
    }


    /**
     * 根据主体Code查询许可信息
     *
     * @return
     */
    @RequestMapping(value = "/xk/search/code", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getXKByCode(String basecredit, String code) {
        BaseResultAPI result = new BaseResultAPI();
        if (code == null || "".equals(code.trim())) {
            result.setResultCode(BaseResultAPI.ERROR_CODE);
            result.setResultInfo("CODE不能为null");
        } else {
            try {
                QueryResult qresult = search.getXK(basecredit, null, null,
                        null, null, null, null,
                        null, 100, 1, code);
                if (qresult != null) {
                    result.setCount(qresult.getCount() == null ? 0 : qresult
                            .getCount().intValue());
                    result.setDatas(qresult.getResult());
                }
                result.setResultCode(BaseResultAPI.SUCCESS_CODE);
            } catch (Exception e) {
                result.setResultCode(BaseResultAPI.ERROR_CODE);
                result.setResultInfo(BaseResultAPI.ERROR_MSG);
                e.printStackTrace();
            }
        }
        return new Gson().toJson(result);
    }

    /**
     * 根据条件查询行政处罚
     *
     * @return
     */
    @RequestMapping(value = "/cf/search/condation", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getCF(String cf_xdr_mc, String cf_wsh, String cf_xdr_shxym,
                        String area_code, String areatype,
                        String cf_xzjg, String basecredit, Integer pagesize, Integer pagenum) {
        BaseResultAPI result = new BaseResultAPI();
        try {
            if (pagesize == null) {
                pagesize = 10;
            }
            if (pagenum == null) {
                pagenum = 1;
            }
            QueryResult qresult = search.getCF(basecredit, cf_xdr_mc, cf_wsh,
                    cf_xdr_shxym, area_code, areatype, cf_xzjg, null, pagesize,
                    pagenum, null);
            if (qresult != null) {
                result.setCount(qresult.getCount() == null ? 0 : (qresult
                        .getCount().intValue() > 10000 ? 10000 : qresult
                        .getCount().intValue()));
                result.setDatas(qresult.getResult());
            }
            result.setResultCode(BaseResultAPI.SUCCESS_CODE);
        } catch (Exception e) {
            result.setResultCode(BaseResultAPI.ERROR_CODE);
            result.setResultInfo(BaseResultAPI.ERROR_MSG);
            e.printStackTrace();
        }
        return new Gson().toJson(result);
    }



    /**
     * 行政处罚查询详情
     *
     * @return
     */
    @RequestMapping(value = "/cf/search/detial", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getCFDetial(String basecredit, String id) {
        BaseResultAPI result = new BaseResultAPI();
        try {
            QueryResult qresult = search.getCF(basecredit, null, null, null,
                    null, null, null, id, 1, 1, null);

            if (qresult != null) {
                result.setCount(qresult.getCount() == null ? 0 : qresult
                        .getCount().intValue());
                result.setDatas(qresult.getResult());
            }
            result.setResultCode(BaseResultAPI.SUCCESS_CODE);
        } catch (Exception e) {
            result.setResultCode(BaseResultAPI.ERROR_CODE);
            result.setResultInfo(BaseResultAPI.ERROR_MSG);
            e.printStackTrace();
        }
        return new Gson().toJson(result);
    }

    /**
     * 行政处罚查询详情
     *
     * @return
     */
    @RequestMapping(value = "/cf/search/code", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getCFByCode(String basecredit, String code) {
        BaseResultAPI result = new BaseResultAPI();
        if (code == null || "".equals(code.trim())) {
            result.setResultCode(BaseResultAPI.ERROR_CODE);
            result.setResultInfo("CODE不能为null");
        } else {
            try {
                QueryResult qresult = search.getCF(basecredit, null, null,
                        null, null, null,
                        null, null, 100, 1, code);
                if (qresult != null) {
                    result.setCount(qresult.getCount() == null ? 0 : qresult
                            .getCount().intValue());
                    result.setDatas(qresult.getResult());
                }
                result.setResultCode(BaseResultAPI.SUCCESS_CODE);
            } catch (Exception e) {
                result.setResultCode(BaseResultAPI.ERROR_CODE);
                result.setResultInfo(BaseResultAPI.ERROR_MSG);
                e.printStackTrace();
            }
        }
        return new Gson().toJson(result);
    }





    /**
     * 根据关键字查询行政许可
     *
     * @return
     */
    @RequestMapping(value = "/xk/search", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String searchXK(String keywords, Integer pagesize, Integer pagenum,
                           String areacode, String areatype) {
        BaseResultAPI result = new BaseResultAPI();
        try {
            if (pagesize == null) {
                pagesize = 10;
            }
            if (pagenum == null) {
                pagenum = 1;
            }
            if (keywords!=null) {
                keywords = URLDecoder.decode(keywords, "utf-8");
            }
            QueryResult qresult = search.searchXKWithKeyWords(keywords, pagesize, pagenum,
                    areacode, areatype);
            if (qresult != null) {
                result.setCount(qresult.getCount() == null ? 0 : (qresult
                        .getCount().intValue() > 10000 ? 10000 : qresult
                        .getCount().intValue()));
                result.setDatas(qresult.getResult());
            }
            result.setResultCode(BaseResultAPI.SUCCESS_CODE);
        } catch (Exception e) {
            result.setResultCode(BaseResultAPI.ERROR_CODE);
            result.setResultInfo(BaseResultAPI.ERROR_MSG);
            e.printStackTrace();
        }
        return new Gson().toJson(result);
    }


    /**
     * 行政许可查询
     *
     * @return
     */
    @RequestMapping(value = "/cf/search", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String searchCF(String keywords, Integer pagesize, Integer pagenum,
                           String areacode, String areatype) {
        BaseResultAPI result = new BaseResultAPI();
        try {
            if (pagesize == null) {
                pagesize = 10;
            }
            if (pagenum == null) {
                pagenum = 1;
            }
            if (keywords!=null) {
                keywords = URLDecoder.decode(keywords, "utf-8");
            }
            QueryResult qresult = search.searchCFWithKeyWords(keywords, pagesize, pagenum,
                    areacode, areatype);
            if (qresult != null) {
                result.setCount(qresult.getCount() == null ? 0 : (qresult
                        .getCount().intValue() > 10000 ? 10000 : qresult
                        .getCount().intValue()));
                result.setDatas(qresult.getResult());
            }
            result.setResultCode(BaseResultAPI.SUCCESS_CODE);
        } catch (Exception e) {
            result.setResultCode(BaseResultAPI.ERROR_CODE);
            result.setResultInfo(BaseResultAPI.ERROR_MSG);
            e.printStackTrace();
        }
        return new Gson().toJson(result);
    }





}
