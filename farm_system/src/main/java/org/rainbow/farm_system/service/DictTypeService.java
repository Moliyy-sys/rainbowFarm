package org.rainbow.farm_system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.rainbow.farm_common.exception.BusException;
import org.rainbow.farm_common.result.CodeEnum;
import org.rainbow.farm_common.util.SecurityUtil;
import org.rainbow.farm_system.entity.DictType;
import org.rainbow.farm_system.mapper.DictTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 字典类型Service
 */
@Service
@Transactional
public class DictTypeService {
    @Autowired
    private DictTypeMapper dictTypeMapper;

    /**
     * 分页查询字典类型
     * @param page 当前页
     * @param size 每页显示条数
     * @param dictName 字典名称
     * @param status 状态
     * @return 字典类型分页数据
     */
    public IPage<DictType> findDictTypePage(int page, int size, String dictName, String status) {
        Page<DictType> pageObj = new Page<>(page, size);
        QueryWrapper<DictType> queryWrapper = new QueryWrapper<>();

        if (StringUtils.hasText(dictName)) {
            queryWrapper.like("dict_name", dictName);
        }
        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");
        return dictTypeMapper.selectPage(pageObj, queryWrapper);
    }

    /**
     * 根据id查询字典类型详情
     * @param id 字典类型ID
     * @return 字典类型详情
     */
    public DictType findById(Long id) {
        return dictTypeMapper.selectById(id);
    }

    /**
     * 新增字典类型
     * @param dictType 字典类型信息
     * @return true成功，false失败
     */
    public boolean addDictType(DictType dictType) {
        // 检查字典名称是否存在
        QueryWrapper<DictType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_name", dictType.getDictName());
        DictType existDictType = dictTypeMapper.selectOne(queryWrapper);
        if (existDictType != null) {
            throw new BusException(CodeEnum.DICT_TYPE_EXIST);
        }

        // 设置默认值
        dictType.setStatus("0"); // 默认正常

        String userName = SecurityUtil.getUserName();
        dictType.setCreateBy(userName);
        dictType.setUpdateBy(userName);
        dictType.setCreateTime(LocalDateTime.now());
        dictType.setUpdateTime(LocalDateTime.now());
        return dictTypeMapper.insert(dictType) > 0;
    }

    /**
     * 修改字典类型
     * @param dictType 字典类型信息
     * @return true成功，false失败
     */
    public boolean updateDictType(DictType dictType) {
        // 检查字典名称是否存在(排除自己)
        QueryWrapper<DictType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_name", dictType.getDictName())
                .ne("dict_id", dictType.getDictId());
        DictType existDictType = dictTypeMapper.selectOne(queryWrapper);
        if (existDictType != null) {
            throw new BusException(CodeEnum.DICT_TYPE_EXIST);
        }

        String userName = SecurityUtil.getUserName();
        dictType.setUpdateBy(userName);
        dictType.setUpdateTime(LocalDateTime.now());
        return dictTypeMapper.updateById(dictType) > 0;
    }

    /**
     * 删除字典类型
     * @param ids 字典类型ID列表
     * @return true成功，false失败
     */
    public boolean deleteDictType(List<Long> ids) {
        return dictTypeMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 查询所有有效的字典类型
     * @return
     */
    public List<DictType> findAll() {
        QueryWrapper<DictType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",0);
        return dictTypeMapper.selectList(queryWrapper);
    }
}
