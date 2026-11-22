$ErrorActionPreference = "Stop"

$root = (Get-Location).Path
$sourcePath = Join-Path $root "AeroSwiftBoard_Project_Report.source.html"
$expandedPath = Join-Path $env:TEMP "AeroSwiftBoard_Project_Report.full.html"
$pdfPath = Join-Path $root "AeroSwiftBoard_Project_Report.pdf"

$html = [System.IO.File]::ReadAllText($sourcePath, [System.Text.Encoding]::UTF8)
if ($html.Contains("<!--LITERATURE_RESEARCH_PLACEHOLDER-->") -or
    $html.Contains("<!--REFERENCES_PLACEHOLDER-->")) {
    throw "Literature references have not been added to the report source."
}

$sourceFiles = @()
$sourceFiles += Get-ChildItem (Join-Path $root "src\main\java") -Recurse -File -Filter "*.java"
$sourceFiles += Get-ChildItem (Join-Path $root "src\test\java") -Recurse -File -Filter "*.java"
$staticRoot = Join-Path $root "src\main\resources\static"
$sourceFiles += Get-ChildItem $staticRoot -File |
    Where-Object { $_.Extension -in @(".html", ".css", ".js") }
$sourceFiles = $sourceFiles | Sort-Object FullName

$sourceAppendix = [System.Text.StringBuilder]::new()
foreach ($file in $sourceFiles) {
    $relativePath = $file.FullName.Substring($root.Length).TrimStart("\")
    $contents = [System.IO.File]::ReadAllText($file.FullName, [System.Text.Encoding]::UTF8)
    [void]$sourceAppendix.Append("<h2>")
    [void]$sourceAppendix.Append([System.Net.WebUtility]::HtmlEncode($relativePath))
    [void]$sourceAppendix.Append("</h2><pre>")
    [void]$sourceAppendix.Append([System.Net.WebUtility]::HtmlEncode($contents))
    [void]$sourceAppendix.Append("</pre>")
}

$html = $html.Replace("<!--SOURCE_APPENDIX-->", $sourceAppendix.ToString())
[System.IO.File]::WriteAllText($expandedPath, $html, [System.Text.Encoding]::UTF8)

$word = $null
$document = $null
try {
    $word = New-Object -ComObject Word.Application
    $word.Visible = $false
    $word.DisplayAlerts = 0
    $document = $word.Documents.Open($expandedPath, $false, $false)

    $document.PageSetup.PaperSize = 7
    $document.PageSetup.TopMargin = 72
    $document.PageSetup.BottomMargin = 72
    $document.PageSetup.RightMargin = 72
    $document.PageSetup.LeftMargin = 108

    $normal = $document.Styles.Item("Normal")
    $normal.Font.Name = "Times New Roman"
    $normal.Font.Size = 12
    $normal.ParagraphFormat.LineSpacingRule = 1
    $normal.ParagraphFormat.SpaceAfter = 6
    $document.Styles.Item("Heading 1").Font.Name = "Times New Roman"
    $document.Styles.Item("Heading 1").Font.Size = 16
    $document.Styles.Item("Heading 2").Font.Name = "Times New Roman"
    $document.Styles.Item("Heading 2").Font.Size = 14
    $document.Styles.Item("Heading 3").Font.Name = "Times New Roman"
    $document.Styles.Item("Heading 3").Font.Size = 12

    foreach ($paragraph in $document.Paragraphs) {
        $text = $paragraph.Range.Text.Trim()
        if ($text -match "^(CHAPTER [1-6]\b|REFERENCES$|APPENDIX [A-D]\b|PROJECT CERTIFICATE$|DECLARATION$|ACKNOWLEDGEMENT$|ABSTRACT$|TABLE OF CONTENTS$|LIST OF FIGURES$|LIST OF TABLES$|ABBREVIATIONS$)") {
            $paragraph.Range.Style = $document.Styles.Item("Heading 1")
        } elseif ($text -match "^(\d+\.\d+\s|[A-D]\.\d+\s)") {
            $paragraph.Range.Style = $document.Styles.Item("Heading 2")
        }
    }

    function Add-ContinuousSectionBefore {
        param($doc, [string]$searchText, [bool]$searchBackward)
        $range = $doc.Content.Duplicate()
        $range.Find.ClearFormatting()
        $range.Find.Text = $searchText
        $range.Find.Forward = -not $searchBackward
        $range.Find.Wrap = 0
        if (-not $range.Find.Execute()) {
            throw "Could not find section marker: $searchText"
        }
        $range.Collapse(1)
        $range.InsertBreak(3)
    }

    Add-ContinuousSectionBefore $document "PROJECT CERTIFICATE" $false
    Add-ContinuousSectionBefore $document "CHAPTER 1 — INTRODUCTION" $true

    $tocRange = $document.Content.Duplicate()
    $tocRange.Find.ClearFormatting()
    $tocRange.Find.Text = "[[TOC]]"
    $tocRange.Find.Wrap = 0
    if (-not $tocRange.Find.Execute()) {
        throw "Could not find the table-of-contents marker."
    }
    $tocRange.Text = ""
    $tocRange.Fields.Add($tocRange, 13, 'TOC \o "1-3" \h \z \u', $true) | Out-Null

    if ($document.Sections.Count -ne 3) {
        throw "Expected three page-numbering sections; found $($document.Sections.Count)."
    }
    foreach ($sectionNumber in 2, 3) {
        $footer = $document.Sections.Item($sectionNumber).Footers.Item(1)
        $footer.LinkToPrevious = $false
        $footer.Range.ParagraphFormat.Alignment = 1
        $footer.PageNumbers.Add(1, $true) | Out-Null
        $footer.PageNumbers.RestartNumberingAtSection = $true
        $footer.PageNumbers.StartingNumber = 1
        if ($sectionNumber -eq 2) {
            $footer.PageNumbers.NumberStyle = 2
        } else {
            $footer.PageNumbers.NumberStyle = 0
        }
    }

    foreach ($toc in $document.TablesOfContents) {
        $toc.Update()
    }
    $document.Fields.Update() | Out-Null
    $document.ExportAsFixedFormat($pdfPath, 17)
    $pageCount = $document.ComputeStatistics(2)
    Write-Output "PDF created: $pdfPath"
    Write-Output "Page count: $pageCount"
    Write-Output "Included source files: $($sourceFiles.Count)"
} finally {
    if ($document -ne $null) {
        $document.Close($false)
    }
    if ($word -ne $null) {
        $word.Quit()
    }
}
