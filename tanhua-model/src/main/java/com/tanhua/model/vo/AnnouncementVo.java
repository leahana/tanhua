package com.tanhua.model.vo;

import com.tanhua.model.domain.Announcement;
import com.tanhua.model.domain.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementVo extends BasePojo {
    private String id;
    private String title;
    private String description;
    private String createDate;

    public static AnnouncementVo init(Announcement announcement) {
        AnnouncementVo vo = new AnnouncementVo();
        BeanUtils.copyProperties(announcement, vo);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date created = announcement.getCreated();
        vo.setCreateDate(sdf.format(created));
        return vo;
    }
}
