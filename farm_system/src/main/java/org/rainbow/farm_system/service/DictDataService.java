package org.rainbow.farm_system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.rainbow.farm_common.exception.BusException;
import org.rainbow.farm_common.result.CodeEnum;
import org.rainbow.farm_common.util.SecurityUtil;
import org.rainbow.farm_system.entity.DictData;
import org.rainbow.farm_system.mapper.DictDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 字典数据Service
 */
@Service
@Transactional
public class DictDataService {
    @Autowired
    private DictDataMapper dictDataMapper;

    /**
     * 分页查询字典数据
     * @param page 当前页
     * @param size 每页显示条数
     * @param dictLabel 字典标签
     * @param dictType 字典类型ID
     * @param status 状态
     * @return 字典数据分页数据
     */
    public IPage<DictData> findDictDataPage(int page, int size, String dictLabel, Long dictType, String status) {
        Page<DictData> pageObj = new Page<>(page, size);
        QueryWrapper<DictData> queryWrapper = new QueryWrapper<>();

        if (StringUtils.hasText(dictLabel)) {
            queryWrapper.like("dict_label", dictLabel);
        }
        if (dictType != null) {
            queryWrapper.eq("dict_type", dictType);
        }
        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByAsc("dict_type");
        queryWrapper.orderByAsc("dict_sort");
        return dictDataMapper.selectPage(pageObj, queryWrapper);
    }

    /**
     * 根据id查询字典数据详情
     * @param id 字典数据ID
     * @return 字典数据详情
     */
    public DictData findById(Long id) {
        return dictDataMapper.selectById(id);
    }

    /**
     * 根据字典类型查询字典数据信息
     * @param dictType 字典类型ID
     * @return 字典数据列表
     */
    public List<DictData> findDictDataByType(Long dictType) {
        return dictDataMapper.selectDictDataByType(dictType);
    }

    /**
     * 新增字典数据
     * @param dictData 字典数据信息
     * @return true成功，false失败
     */
    public boolean addDictData(DictData dictData) {
        // 检查字典数据是否已存在（同一类型下，标签或值重复）
        QueryWrapper<DictData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_type", dictData.getDictType())
                .and(wrapper -> wrapper.eq("dict_label", dictData.getDictLabel())
                        .or().eq("dict_value", dictData.getDictValue()));
        DictData existDictData = dictDataMapper.selectOne(queryWrapper);
        if (existDictData != null) {
            throw new BusException(CodeEnum.DICT_DATA_EXIST);
        }
        // 设置默认值
        dictData.setCreateTime(LocalDateTime.now());
        dictData.setStatus("0"); // 默认正常
        dictData.setIsDefault("N"); // 默认否
        String userName = SecurityUtil.getUserName();
        dictData.setCreateBy(userName);
        dictData.setUpdateBy(userName);
        dictData.setUpdateTime(LocalDateTime.now());
        return dictDataMapper.insert(dictData) > 0;
    }

    /**
     * 修改字典数据
     * @param dictData 字典数据信息
     * @return true成功，false失败
     */
    public boolean updateDictData(DictData dictData) {
        // 检查字典数据是否已存在(排除自己)
        QueryWrapper<DictData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_type", dictData.getDictType())
                .and(wrapper -> wrapper.eq("dict_label", dictData.getDictLabel())
                        .or().eq("dict_value", dictData.getDictValue()))
                .ne("dict_code", dictData.getDictCode());
        DictData existDictData = dictDataMapper.selectOne(queryWrapper);
        if (existDictData != null) {
            throw new BusException(CodeEnum.DICT_DATA_EXIST);
        }
        dictData.setUpdateTime(LocalDateTime.now());
        return dictDataMapper.updateById(dictData) > 0;
    }

    /**
     * 删除字典数据
     * @param idList 字典数据ID列表
     * @return true成功，false失败
     */
    public boolean deleteDictData(List<Long> idList) {
        return dictDataMapper.deleteBatchIds(idList) > 0;
    }
}
