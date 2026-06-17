package com.mes.admin.modules.mes.document.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.modules.mes.document.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * MES 文档管理控制器
 * 
 * 提供：
 * - 上传文件（Word / Excel / PPT / PDF 等）
 * - 文档格式转换（Office → PDF，基于 OpenOffice / LibreOffice）
 * - 文档列表 / 最近文件
 */
@RestController
@RequestMapping("/api/mes/document")
@CrossOrigin
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    /**
     * 上传文件
     * POST /api/mes/document/upload
     * Form-Data: file (必填)
     */
    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        return documentService.upload(file);
    }

    /**
     * 将 Office 文档转换为 PDF
     * POST /api/mes/document/convert?fileName=xxx.docx
     */
    @PostMapping("/convert")
    public Result<Map<String, Object>> convertToPdf(@RequestParam("fileName") String fileName) {
        return documentService.convertToPdf(fileName);
    }

    /**
     * 获取最近文档列表（便于前端管理）
     * GET /api/mes/document/list
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> listDocuments() {
        return documentService.listDocuments();
    }
}
