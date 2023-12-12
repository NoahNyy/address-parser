package org.nyy;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.text.StrJoiner;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 地址解析
 *
 * @author niuyy
 * @date 2023-12-12
 */
public class AddressParser {
    public static void main(String[] args) throws IOException {
        JSONObject jsonObject = readAddressFile();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Map<String, Future<?>> provinceToFuture = MapUtil.newHashMap();
        jsonObject.forEach((province, cities) -> {
            Future<?> submit = executorService.submit(() -> {
                JSONObject cityJson = JSONUtil.parseObj(JSONUtil.toJsonStr(cities));
                cityJson.forEach((city, districts) -> {
                    List<String> districtList = JSONUtil.toList(JSONUtil.toJsonStr(districts), String.class);
                    districtList.forEach(district -> {
                        String addressText = province + city + district;
                        try {
                            Address expected = new Address(province, city, district, "");
                            Optional<Address> addressDtoOpt = AddressUtil.parseByRegex(addressText);
                            Assert.isTrue(addressDtoOpt.isPresent(), "解析失败！");
                            Address addressDTO = addressDtoOpt.get();
                            Assert.isTrue(expected.equals(addressDTO), StrFormatter.format("不匹配，text：{}，result：{}，expect：{}", addressText, addressDTO.toString(), expected.toString()));
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    });
                });
                System.out.println(province + " done");
            });
            provinceToFuture.put(province, submit);
        });

        new Thread(() -> {
            boolean stop = false;
            List<String> list = ListUtil.toList();
            while (!stop) {
                provinceToFuture.forEach((p, f) -> {
                    if (!f.isDone()) {
                        list.add(p);
                    }
                });
                System.out.println(StrJoiner.of(", ").append(list).toString());
                list.clear();
                try {
                    TimeUnit.SECONDS.sleep(3L);
                } catch (InterruptedException e) {
                }

                stop = true;
                for (Future<?> f : provinceToFuture.values()) {
                    if (!f.isDone()) {
                        stop = false;
                        break;
                    }
                }
            }
            System.out.println("===解析完成===");
            executorService.shutdown();
        }).start();
    }

    private static JSONObject readAddressFile() throws IOException {
        URL resource = AddressParser.class.getClassLoader().getResource("address.json");
        Assert.notNull(resource, "文件 address.json 未找到");

        try (BufferedReader br = new BufferedReader(new FileReader(resource.getFile()))) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            return JSONUtil.parseObj(sb.toString());
        }
    }
}
