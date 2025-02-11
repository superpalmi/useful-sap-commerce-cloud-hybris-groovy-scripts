import com.azure.storage.blob.*
import java.nio.file.*
//NOTE: azure hotfolders extensions are needed, please see the SAP Documentation for more information

// This script connects to an Azure Blob Storage container and lists all the blobs in the container
def connectionString = "your-connection-string"
def containerName = "your-container-name" // usually it's called "hybris"
def hotfolderPath = "/your/hotfolder/path/" // usually it is "/hybris/hotfolder"

def blobServiceClient = new BlobServiceClientBuilder()
        .connectionString(connectionString)
        .buildClient()

def containerClient = blobServiceClient.getBlobContainerClient(containerName)

containerClient.listBlobs().each { blobItem ->
    def blobName = blobItem.getName()
    def blobClient = containerClient.getBlobClient(blobName)

    println "Folder contains: $blobName"
}

println "Script completed!"