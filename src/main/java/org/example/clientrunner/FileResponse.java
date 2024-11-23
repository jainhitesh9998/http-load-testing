package org.example.clientrunner;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class FileResponse {
    private boolean success;
    private String fileName;
    private String error;
}