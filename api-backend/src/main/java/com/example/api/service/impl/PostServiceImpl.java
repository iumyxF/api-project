package com.example.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.common.ErrorCode;
import com.example.api.constant.SystemConstant;
import com.example.api.exception.BusinessException;
import com.example.api.mapper.PostMapper;
import com.example.api.model.entity.Post;
import com.example.api.model.enums.PostGenderEnum;
import com.example.api.model.enums.PostReviewStatusEnum;
import com.example.api.service.PostService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author iumyxFli
 * @description 针对表【post(帖子)】的数据库操作Service实现
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Override
    public void validPost(Post post, boolean add) {
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer age = post.getAge();
        Integer gender = post.getGender();
        String content = post.getContent();
        String job = post.getJob();
        String place = post.getPlace();
        String education = post.getEducation();
        String loveExp = post.getLoveExp();
        Integer reviewStatus = post.getReviewStatus();
        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(content, job, place, education, loveExp) || ObjectUtils.anyNull(age, gender)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(content) && content.length() > SystemConstant.MAX_CONTENT_SIZE) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (reviewStatus != null && !PostReviewStatusEnum.getValues().contains(reviewStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (age != null && (age < SystemConstant.MIN_AGE || age > SystemConstant.MAX_AGE)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "年龄不符合要求");
        }
        if (gender != null && !PostGenderEnum.getValues().contains(gender)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "性别不符合要求");
        }
    }
}




