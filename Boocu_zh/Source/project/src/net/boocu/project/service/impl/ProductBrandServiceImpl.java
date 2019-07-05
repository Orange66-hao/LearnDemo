package net.boocu.project.service.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.boocu.project.dao.ProductBrandDao;
import net.boocu.project.entity.ExportExcelSeedBack;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.service.ProductBrandService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("productBrandServiceImpl")
public class ProductBrandServiceImpl extends BaseServiceImpl<ProductBrandEntity, Long> implements ProductBrandService {
	@Resource(name = "productBrandDaoImpl")
    private ProductBrandDao productBrandDao;
    
    @Resource(name = "productBrandDaoImpl")
    public void setBaseDao(ProductBrandDao productBrandDao) {
        super.setBaseDao(productBrandDao);
    }

    @Override
    public void orderExport(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        List<Map<String, Object>> orderList = productBrandDao.querybrandrows(request, response, null);//查询数据
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

        if (orderList != null && orderList.size() > 0) {//查询的数据不为空就对数据进行导出
            //导出文件的标题
            String title = "品牌数据统计" + df.format(new Date()) + ".xls";
            //设置表格标题行
            String[] headers = new String[]{"序号", "品牌名称(中文)", "品牌名称(英文)","国家"};
            List<Object[]> dataList = new ArrayList<Object[]>();
            Object[] objs = null;

            for (int i=0;i<orderList.size();i++) {
                Map<String, Object> entity=orderList.get(i);
                objs = new Object[headers.length];

                objs[0] = i;
                if (entity.get("name") != null && entity.get("name") != "") {
                    objs[1] = entity.get("name");
                }
                if ((entity.get("name_en") != null) && (entity.get("name_en") != "")) {
                    objs[2] = entity.get("name_en");
                }
                if ((entity.get("country") != null) && (entity.get("country") != "")) {
                    objs[3] = entity.get("country");
                }
                dataList.add(objs);
            }

            //使用流将数据导出
            OutputStream out = null;
            try {
                //防止中文乱码
                String headStr = "attachment; filename=\"" + new String(title.getBytes("gb2312"), "ISO8859-1") + "\"";
                response.setContentType("octets/stream");
                response.setContentType("APPLICATION/OCTET-STREAM");
                response.setHeader("Content-Disposition", headStr);
                out = response.getOutputStream();
                //ExportExcel ex = new ExportExcel(title, headers, dataList);//有标题
                ExportExcelSeedBack ex = new ExportExcelSeedBack(title, headers, dataList);//没有标题
                ex.export(out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Page<Map<String, Object>> findPage(Pageable pageable,HttpServletRequest request) {
        return productBrandDao.findPage(pageable,request);
    }
}
