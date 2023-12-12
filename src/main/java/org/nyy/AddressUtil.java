package org.nyy;

import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author niuyy
 * @date 2023-12-12
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressUtil {

    private static final Pattern ADDRESS_PATTERN = Pattern.compile("(?<province>北京|上海|天津|重庆|台湾|[^特别行政区]+特别行政区|[^自治区]+自治区|[^省]+省|[^市]+市)" +
            "(?<city>省直辖行政单位|省属虚拟市|市辖县|市辖区|省直辖县级行政区划|县|自治州|上海城区|北京城区|天津城区|重庆城区|重庆郊县|花莲县|澎湖县|桃园县|苗栗县|南投县|彰化县|云林县|宜兰县|屏东县|台东县|新竹县|嘉义县|神农架林区|中沙群岛的岛礁及其海域|普洱市|海西蒙古族藏族自治州|[^市]+自治州|[^自治州]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|香港岛|九龙|新界|.*?市|.*?县|[^岛]+岛)?" +
            "(?<county>前镇区|沙县区|济源市.+?镇|济源市(沁园|济水|北海|天坛|玉泉)街道|兵团[零一二三四五六七八九十]{1,3}团|海西蒙古族藏族自治州直辖|石岐区街道|松山湖管委会|虎门港管委会|东莞生态园|东区街道|湾仔区|古镇镇|西区街道|钓鱼台|兵团一牧场|南区街道|华南热作学院|国营尖峰岭林业公司|兵团图木舒克市永安坝|兵团图木舒克市喀拉拜勒镇|火炬开发区街道|海南矿业联合有限公司|国营黎母山林业公司|英州镇|国营吊罗山林业公司|海南保亭热带作物研究所|津市市|太鲁阁|沉湖管委会|中心监狱|老街街道|杨市街道|兵团第一师水利水电工程处|兵团第一师塔里木灌区水利管理处|江汉石油管理局|兵团第一师幸福农场|兵团第一师塔里木灌区|兵团皮山农场|五五新镇街道|辉县市|平镇区|梅县区|左镇区|赣县区|中沙群岛的岛礁及其海域|湾仔|[^市区]+市区|[^市镇]+市镇|[^县]+县|[^市]+市|[^镇]+镇|[^区]+区|[^乡]+乡|[^团]+团|[^街道]+街道|.+场|.+旗|.+海域|.+岛)?(?<address>.*)");

    /**
     * 解析地址
     * @param addressText 地址文本
     * @return 地址
     */
    public static Optional<Address> parseByRegex(String addressText) {
        if (StrUtil.isBlank(addressText)) {
            return Optional.empty();
        }
        Matcher matcher = ADDRESS_PATTERN.matcher(addressText);
        Address address = new Address();
        while (matcher.find()) {
            address.setProvince(matcher.group("province") == null ? "" : matcher.group("province").trim());
            address.setCity(matcher.group("city") == null ? "" : matcher.group("city").trim());
            address.setDistrict(matcher.group("county") == null ? "" : matcher.group("county").trim());
            address.setAddress(matcher.group("address") == null ? "" : matcher.group("address").trim());
        }
        return Optional.of(address);
    }
}
