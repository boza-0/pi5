# Run in PowerShell from the project root or set $root to the pi5 path
$root = 'C:\Users\ricar\Desktop\pi5'

function Show-Tree {
    param($path, $indent = '')
    Get-ChildItem -LiteralPath $path | Where-Object { $_.Name -notin @('target','node_modules') } | Sort-Object @{Expression={$_.PSIsContainer};Descending=$true}, Name |
    ForEach-Object {
        if ($_.PSIsContainer) {
            Write-Output ("{0}{1}/" -f $indent, $_.Name)
            Show-Tree -path $_.FullName -indent ($indent + '  ')
        } else {
            Write-Output ("{0}{1}" -f $indent, $_.Name)
        }
    }
}

Write-Output ($root + '/')
Show-Tree -path $root